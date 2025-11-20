package com.example.config;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.FilterConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class SessionFilter implements Filter {

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        // 过滤器初始化逻辑
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        System.out.println("Cookies: " + httpRequest.getHeader("Cookie"));
        // 获取会话 ID，注意使用 getSession(false) 防止创建新的会话
        HttpSession session = httpRequest.getSession(false);
        //System.out.println(session);
        if (session != null) {
            String sessionId = session.getId();
            System.out.println("Session ID: " + sessionId);
            // 可以将 sessionId 放入请求上下文中，供后续使用
            request.setAttribute("sessionId", sessionId);
        } else {
            System.out.println("No session found.");
        }

        // 继续请求的处理
        chain.doFilter(request, response);
    }

    @Override
    public void destroy() {
        // 过滤器销毁时的处理
    }
}
