package com.example.controller;

import com.example.service.SmsCaptchaService;
import com.example.util.PhoneNumberValidator;
import com.example.util.SmsSender;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/Sms")
public class SmsController {

    @Autowired
    private SmsCaptchaService smsCaptchaService;
    // 辅助方法
    private static String getEnvOrGenerate(String envVarName) {
        String value = System.getenv(envVarName);
        if (value == null || value.isEmpty()) {
            return "false";
        }
        return value;
    }

    @PostMapping("/Sender")
    public Map<String, Object> sendSms(@RequestBody Map<String, String> payload) {
        Map<String, Object> response = new HashMap<>();
        String phoneNumber = payload.get("phoneNumber"); // 从 JSON 数据中获取参数

        //判断手机号是否合法
        Boolean phoneNumberValid = null;
        if (PhoneNumberValidator.isValidPhoneNumber(phoneNumber)) {
            phoneNumberValid = true;
        } else {
            response.put("message", "md你输入的是手机号吗！！！！");
            phoneNumberValid = false;
        }
        System.out.println(phoneNumberValid);
        if (phoneNumberValid) {
            String smsCode = smsCaptchaService.generateCode();  // 生成验证码

            try {
                System.out.println("电话号码：" + phoneNumber);
                System.out.println("验证码: " + smsCode);
                // 存储验证码
                smsCaptchaService.storeCaptcha(phoneNumber, smsCode);
                if (getEnvOrGenerate("ENABLESMS").equals("true")) {
                    // 调用阿里云短信 API 发送验证码
                    System.out.println("已开启短信发送");
                    SmsSender.sendSms(phoneNumber, smsCode);
                } else {
                    System.out.println("已关闭短信发送，验证码返回到控制台");
                    response.put("code", smsCode);
                }
                response.put("success", true);
                response.put("message", "验证码已发送");
            } catch (Exception e) {
                response.put("success", false);
                response.put("message", "短信验证码发送失败");
            }
        }
        return response;
    }


    @PostMapping("/Verify")
    public Map<String, Object> verifyCode(@RequestBody Map<String, String> requestBody) {
        String phoneNumber = requestBody.get("phoneNumber");
        String smsCode = requestBody.get("smsCode");
        Map<String, Object> response = new HashMap<>();
        // 验证码校验
        if (smsCaptchaService.verifyCaptcha(phoneNumber, smsCode)) {
            response.put("success", true);
            response.put("message", "验证码正确");
            smsCaptchaService.removeCaptcha(phoneNumber); // 验证通过后删除验证码
        } else {
            response.put("success", false);
            response.put("message", "验证码错误或已过期");
        }
        return response;
    }
}
