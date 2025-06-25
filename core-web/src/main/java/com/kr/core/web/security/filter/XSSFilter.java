package com.kr.core.web.security.filter;

import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;

import java.io.IOException;

/**
 * XSS 공격 방지 필터
 * HTTP 요청을 XSSRequestWrapper로 감싸서 XSS 공격을 방지합니다.
 * 특정 URL 패턴은 필터링에서 제외할 수 있습니다.
 */
public class XSSFilter implements Filter {

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        // Initialize the filter
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        if (excludeUrl((HttpServletRequest) request)) {
            chain.doFilter(request, response);
        } else {
            chain.doFilter(new XSSRequestWrapper((HttpServletRequest) request), response);
        }
    }

    @Override
    public void destroy() {
        // Destroy the filter
    }

    /**
     * 특정 URL 패턴을 XSS 필터링에서 제외합니다.
     * 바이너리 데이터나 Base64 인코딩된 데이터를 처리하는 엔드포인트는 제외해야 합니다.
     *
     * @param request HTTP 요청
     * @return 제외 여부 (true: 필터링 제외, false: 필터링 적용)
     */
    private boolean excludeUrl(HttpServletRequest request) {
        String uri = request.getRequestURI().toString().trim();
        // 구체적인 예외 URL 패턴 추가
        if (uri.startsWith("/api/files") || uri.startsWith("/record/idle")) {
            return true; // 바이너리 데이터나 Base64 인코딩된 데이터는 XSS 필터링 제외
        }
        return false;
    }
}