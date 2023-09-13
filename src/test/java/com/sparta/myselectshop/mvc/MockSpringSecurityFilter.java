package com.sparta.myselectshop.mvc;

import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.io.IOException;

public class MockSpringSecurityFilter implements Filter {
    @Override
    public void init(FilterConfig filterConfig) {}

    @Override
    public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain) throws IOException, ServletException {
        SecurityContextHolder.getContext() // SecurityContextHolder는 인증객체를 담는 공간이다.
                .setAuthentication((Authentication) ((HttpServletRequest) req).getUserPrincipal()); // 그 안에 Security는 가짜(Mock) 인증객체를 만든다. -> 가짜 인증을 하는행위, -> Security가 동작하면 테스트하는데 방해가 되기 때문에 우회하는방법
        chain.doFilter(req, res);
    }

    @Override
    public void destroy() {
        SecurityContextHolder.clearContext();
    }
}