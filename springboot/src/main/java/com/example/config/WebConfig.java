package com.example.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer { // 实现 WebMvcConfigurer

    @Autowired
    private SessionFilter sessionFilter;

    @Bean
    public FilterRegistrationBean<SessionFilter> sessionFilterRegistration() {
        FilterRegistrationBean<SessionFilter> registrationBean = new FilterRegistrationBean<>();
        registrationBean.setFilter(sessionFilter);
        registrationBean.addUrlPatterns("/*"); // 设置过滤路径，使用 "/*" 而不是 "/**"
        registrationBean.setOrder(1); // 设置过滤器的优先级
        return registrationBean;
    }

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        // 通过 CorsConfiguration 配置 CORS
        registry.addMapping("/api/**")
                .allowedOrigins("http://lilili.qicp.vip:9080", "http://192.168.0.181:9080" )
                .allowedMethods("GET", "POST", "PUT", "DELETE")
                .allowedHeaders("*")
                .allowCredentials(true);

        // 为 /local 路径设置 CORS 规则
        registry.addMapping("/**")
                .allowedOrigins("http://127.0.0.1:5173", "http://localhost:9090")
                .allowedMethods("GET", "POST", "PUT", "DELETE")
                .allowedHeaders("*")
                .allowCredentials(true);
    }
}
