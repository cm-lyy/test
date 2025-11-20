package com.example.util;

import java.util.regex.Pattern;

public class PhoneNumberValidator {
    // 定义手机号正则：适配中国大陆手机号 (11位，1开头，第二位为 3-9)
    private static final String PHONE_NUMBER_REGEX = "^1[3-9]\\d{9}$";

    public static boolean isValidPhoneNumber(String phoneNumber) {
        return Pattern.matches(PHONE_NUMBER_REGEX, phoneNumber);
    }
}

