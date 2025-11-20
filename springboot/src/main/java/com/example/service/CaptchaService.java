package com.example.service;

import com.example.entity.Captcha;
import com.example.mapper.CaptchaMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CaptchaService {

    @Autowired
    private CaptchaMapper captchaMapper;

    // 保存验证码到数据库
    public void saveCaptcha(Captcha captcha) {
        captchaMapper.insertCaptcha(captcha);
    }

    // 使用 sessionId 获取最新的验证码
    public Captcha getLatestCaptchaBySessionId(String sessionId) {
        return captchaMapper.selectLatestCaptchaBySessionId(sessionId);
    }
}
