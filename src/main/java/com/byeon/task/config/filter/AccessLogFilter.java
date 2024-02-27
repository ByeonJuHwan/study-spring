package com.byeon.task.config.filter;

import com.byeon.task.domain.entity.AccessLog;
import com.byeon.task.repository.AccessLogRepository;
import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.time.LocalDateTime;

@Slf4j
@RequiredArgsConstructor
public class AccessLogFilter implements Filter {

    private final AccessLogRepository accessLogRepository;

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) servletRequest;

        String ipAddress = request.getRemoteAddr();
        String userAgent = request.getHeader("User-Agent");
        LocalDateTime requestTime = LocalDateTime.now();
        String requestMethod = request.getMethod();
        String uri = request.getRequestURI();

        AccessLog accessLog = AccessLog.builder()
                .ipAddress(ipAddress)
                .userAgent(userAgent)
                .requestTime(requestTime)
                .method(requestMethod)
                .uri(uri)
                .build();

        AccessLog savedLog = accessLogRepository.save(accessLog);
        log.info("savedLog = {}", savedLog);
        filterChain.doFilter(servletRequest, servletResponse);
    }
}
