package com.byeon.task.config.filter;

import com.byeon.task.domain.entity.AccessLog;
import com.byeon.task.repository.AccessLogRepository;
import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cglib.core.Local;
import org.springframework.web.util.ContentCachingRequestWrapper;
import org.springframework.web.util.ContentCachingResponseWrapper;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
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

        ContentCachingRequestWrapper wrapperRequest = new ContentCachingRequestWrapper(request);
        ContentCachingResponseWrapper wrapperResponse = new ContentCachingResponseWrapper((HttpServletResponse) servletResponse);

        // 시간측정, 시작
        LocalDateTime startTime = LocalDateTime.now();

        filterChain.doFilter(wrapperRequest, wrapperResponse);

        // 시간측정, 종료, 걸린시간 측정
        LocalDateTime endTime = LocalDateTime.now();
        long elapseTime = Duration.between(startTime, endTime).getSeconds();
        log.info("elapseTime = {}", elapseTime);

        //  여기에서 wrapperRequest requestBody를 꺼내서 AccessLog 에 추가.
        byte[] contentRequestAsArray = wrapperRequest.getContentAsByteArray();
        String cashingRequest = new String(contentRequestAsArray, StandardCharsets.UTF_8);
        log.info("cashingRequest = {}", cashingRequest);


        // 여기에서 wrapperResponse responseBody를 꺼내서 AccessLog 에 추가.
        byte[] contentResponseAsArray = wrapperResponse.getContentAsByteArray();
        String cashingResponse = new String(contentResponseAsArray, StandardCharsets.UTF_8);
        log.info("cashingResponse = {}", cashingResponse);

        // 여기에서 위에서 측정한 시간을 AccessLog 에 추가.
        AccessLog accessLog = AccessLog.builder()
                .ipAddress(ipAddress)
                .userAgent(userAgent)
                .requestTime(requestTime)
                .method(requestMethod)
                .uri(uri)
                .requestBody(cashingRequest)
                .responseBody(cashingResponse)
                .elapseTime((int)elapseTime)
                .build();

        AccessLog savedLog = accessLogRepository.save(accessLog);
        log.info("savedLog = {}", savedLog);

        wrapperResponse.copyBodyToResponse();
    }
}
