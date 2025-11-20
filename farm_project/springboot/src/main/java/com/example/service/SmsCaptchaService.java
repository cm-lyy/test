package com.example.service;

import com.example.entity.SmsCaptcha;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

@Service
public class SmsCaptchaService {

    // 使用 Map 存储验证码，手机号与验证码的映射
    private final Map<String, SmsCaptcha> codeStorage = new HashMap<>();

    // 生成 6 位随机验证码
    public String generateCode() {
        Random random = new Random();
        int smsCode = 100000 + random.nextInt(900000);
        return String.valueOf(smsCode);
    }

    // 存储验证码
    public void storeCaptcha(String phoneNumber, String smsCode) {
        SmsCaptcha captcha = new SmsCaptcha();
        captcha.setPhoneNumber(phoneNumber);
        captcha.setCode(smsCode);
        codeStorage.put(phoneNumber, captcha);
    }

    // 验证验证码
    public boolean verifyCaptcha(String phoneNumber, String smsCode) {
        SmsCaptcha storedCaptcha = codeStorage.get(phoneNumber);
        return storedCaptcha != null && storedCaptcha.getCode().equals(smsCode);
    }

    // 删除验证码
    public void removeCaptcha(String phoneNumber) {
        codeStorage.remove(phoneNumber);
    }
}
