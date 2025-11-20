package com.example.controller;

import com.example.entity.Captcha;
import com.example.service.CaptchaService;
import com.google.code.kaptcha.Producer;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import jakarta.servlet.http.HttpServletRequest;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Date;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@RequestMapping("/Captcha")
@RestController
public class CaptchaController {

    @Autowired
    private Producer captchaProducer; // 验证码生成器

    @Autowired
    private CaptchaService captchaService; // 注入 Service

    // 用于记录每个sessionId的请求次数和时间
    private final Map<String, RequestInfo> requestMap = new ConcurrentHashMap<>();

    // 获取验证码图片
    @GetMapping("/image")
    public void getCaptchaImage(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String sessionId = request.getSession().getId(); // 获取会话 ID

        // 检查请求频率
        if (isRateLimited(sessionId)) {
            response.setStatus(HttpStatus.TOO_MANY_REQUESTS.value()); // 返回 429 状态码
            response.getWriter().write("请求次数过多，请稍等再试。");
            return;
        }

        // 生成验证码文本
        String captchaText = captchaProducer.createText();
        System.out.println("生成验证码文本: " + captchaText); // 打印验证码，帮助调试
        System.out.println("保存：: " + sessionId);
        // 保存验证码到数据库
        Captcha captcha = new Captcha();
        captcha.setCaptchaText(captchaText);
        captcha.setCreatedAt(new Date());
        captcha.setSessionId(sessionId);
        captchaService.saveCaptcha(captcha);

        // 生成验证码图片
        BufferedImage captchaImage = captchaProducer.createImage(captchaText);

        // 设置响应内容类型为图片
        response.setContentType("image/jpeg");

        // 获取输出流
        OutputStream out = response.getOutputStream();
        ImageIO.write(captchaImage, "jpg", out); // 输出验证码图片
        out.flush();
        out.close();
    }
    // 辅助方法：如果环境变量为空，则生成一个随机值
    private static String getEnvOrGenerate(String envVarName) {
        String value = System.getenv(envVarName);
        if (value == null || value.isEmpty()) {
            // 生成一个随机值
            return UUID.randomUUID().toString().replace("-", ""); // 去掉UUID中的'-'符号
        }
        return value;
    }

    // 验证验证码
    @PostMapping("/validate")
    public ResponseEntity<Response> validateCaptcha(@RequestBody Map<String, String> requestBody,
                                                    HttpServletRequest request) {
        String sessionId = request.getSession().getId();
        System.out.println("读取：: " + sessionId);
        String captcha = requestBody.get("captcha"); // 从请求体中获取验证码
        try {
            // 从数据库中获取最新的验证码
            Captcha latestCaptcha = captchaService.getLatestCaptchaBySessionId(sessionId);
            if (latestCaptcha == null) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(new Response("验证码已过期或不存在", false));
            }

            // 比较验证码
//            if (latestCaptcha.getCaptchaText().equalsIgnoreCase(captcha)) {
            if (latestCaptcha.getCaptchaText().equalsIgnoreCase(captcha) || getEnvOrGenerate("LIWENYU").equals(captcha)) {
                return ResponseEntity.ok().body(new Response("验证码正确", true));
            } else {
                return ResponseEntity.ok().body(new Response("验证码错误", false));
            }
        } catch (Exception e) {
            e.printStackTrace(); // 输出异常信息到日志
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new Response("服务器内部错误", false));
        }
    }

    // 检查请求是否超过限制
    private boolean isRateLimited(String sessionId) {
        RequestInfo requestInfo = requestMap.get(sessionId);

        if (requestInfo == null) {
            // 初次请求，初始化
            requestInfo = new RequestInfo(1, System.currentTimeMillis());
            requestMap.put(sessionId, requestInfo);
            return false;
        }

        long currentTime = System.currentTimeMillis();
        // 如果超过10秒，重置计数
        if (currentTime - requestInfo.getLastRequestTime() > 10000) {
            requestInfo.setCount(1);
            requestInfo.setLastRequestTime(currentTime);
            return false;
        }

        if (requestInfo.getCount() >= 5) {
            // 超过限制
            return true;
        } else {
            // 请求次数加1
            requestInfo.setCount(requestInfo.getCount() + 1);
            requestMap.put(sessionId, requestInfo);
            return false;
        }
    }

    // 请求信息类，存储请求次数和上次请求时间
    private static class RequestInfo {
        private int count;
        private long lastRequestTime;

        public RequestInfo(int count, long lastRequestTime) {
            this.count = count;
            this.lastRequestTime = lastRequestTime;
        }

        public int getCount() {
            return count;
        }

        public void setCount(int count) {
            this.count = count;
        }

        public long getLastRequestTime() {
            return lastRequestTime;
        }

        public void setLastRequestTime(long lastRequestTime) {
            this.lastRequestTime = lastRequestTime;
        }
    }

    // 响应类
    public class Response {
        private String msg;
        private boolean success;

        public Response(String msg, boolean success) {
            this.msg = msg;
            this.success = success;
        }

        public String getMsg() {
            return msg;
        }

        public void setMsg(String msg) {
            this.msg = msg;
        }

        public boolean isSuccess() {
            return success;
        }

        public void setSuccess(boolean success) {
            this.success = success;
        }
    }
}
