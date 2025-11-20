package com.example.controller;

import com.example.util.EmailSender;
import jakarta.mail.MessagingException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.HttpStatus;
import java.util.HashMap;
import java.util.Map;

@RestController
//@RequestMapping("/email")
public class EmailController {

    private static final Logger logger = LoggerFactory.getLogger(EmailController.class);

    // 定义常量，避免硬编码
    private static final String KEY_TO = "to";
    private static final String KEY_SUBJECT = "subject";
    private static final String KEY_TEXT = "text";

    @Autowired
    private EmailSender emailSender;

    /**
     * 邮件发送接口
     *
     * @param payload 请求体，包含收件人邮箱 (to)、邮件主题 (subject) 和邮件内容 (text)
     * @return 标准化响应，包括状态码 (code)、成功标志 (success)、消息 (message)
     */
    @PostMapping("/send")
    public ResponseEntity<Map<String, Object>> sendEmail(@RequestBody Map<String, String> payload) {
        Map<String, Object> response = new HashMap<>();
        String to = payload.get(KEY_TO);
        String subject = payload.get(KEY_SUBJECT);
        String text = payload.get(KEY_TEXT);

        // 参数校验
        if (to == null || subject == null || text == null) {
            response.put("code", String.valueOf(HttpStatus.BAD_REQUEST.value()));
            response.put("success", false);
            response.put("message", "参数缺失：to, subject 和 text 均不能为空");
            return ResponseEntity.badRequest().body(response);
        }

        try {
            // 调用邮件发送工具类
            emailSender.sendEmail(to, subject, text);
            response.put("code", String.valueOf(HttpStatus.OK.value()));
            response.put("success", true);
            response.put("message", "邮件发送成功");
            return ResponseEntity.ok(response);
        } catch (MessagingException e) {
            logger.error("邮件发送失败：", e);
            response.put("code", String.valueOf(HttpStatus.INTERNAL_SERVER_ERROR.value()));
            response.put("success", false);
            response.put("message", "邮件发送失败：" + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
}
