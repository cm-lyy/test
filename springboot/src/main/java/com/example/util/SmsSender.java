package com.example.util;

import com.aliyun.dysmsapi20170525.Client;
import com.aliyun.dysmsapi20170525.models.SendSmsRequest;
import com.aliyun.dysmsapi20170525.models.SendSmsResponse;
import com.aliyun.teaopenapi.models.Config;
import java.util.UUID;
import static com.aliyun.teautil.Common.toJSONString;

public class SmsSender {
    // 创建阿里云短信服务客户端
    public static Client createClient() throws Exception {
        Config config = new Config()
                .setAccessKeyId(getEnvOrGenerate("SETACCESSKEYID"))
                .setAccessKeySecret(getEnvOrGenerate("SETACCESSKEYSECRET"));
        config.endpoint = "dysmsapi.aliyuncs.com";
        return new Client(config);
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

    // 发送短信的方法
    public static void sendSms(String phoneNumber, String smsCode) {
        try {
            Client client = createClient();

            // 构造请求对象，动态传入参数
            SendSmsRequest sendSmsRequest = new SendSmsRequest()
                    .setPhoneNumbers(phoneNumber)
                    .setSignName("裕裕商城") // 确保已审核通过
                    .setTemplateCode("SMS_475465062") // 确保模板有效
                    .setTemplateParam(String.format("{\"code\":\"%s\"}", smsCode));

            // 发送短信并获取响应
            SendSmsResponse sendSmsResponse = client.sendSms(sendSmsRequest);

            // 打印响应信息
            System.out.println("发送短信成功：" + toJSONString(sendSmsResponse));
        } catch (Exception e) {
            System.err.println("发送短信失败：" + e.getMessage());
            e.printStackTrace();
        }
    }
}

