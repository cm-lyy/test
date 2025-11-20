package com.example.mapper;

import com.example.entity.Captcha;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface CaptchaMapper {

    @Insert("INSERT INTO captcha (captcha_text, session_id, created_at) VALUES (#{captchaText}, #{sessionId}, #{createdAt})")
    void insertCaptcha(Captcha captcha);

    // 查询最新的、与会话 ID 关联的验证码
    @Select("SELECT * FROM captcha WHERE session_id = #{sessionId} ORDER BY created_at DESC LIMIT 1")
    Captcha selectLatestCaptchaBySessionId(String sessionId);
}
