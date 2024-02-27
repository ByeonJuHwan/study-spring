package com.byeon.task.config.filter;

import com.byeon.task.service.TelegramService;
import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.ContentCachingResponseWrapper;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

@Slf4j
@RequiredArgsConstructor
public class ExceptionCashingFilter implements Filter {

    @Value("${telegram.key}")
    private String telegramKey;

    @Value("${telegram.chatId}")
    private String chatId;

    private final ApplicationContext applicationContext;

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse response, FilterChain filterChain) throws IOException, ServletException {

        ContentCachingResponseWrapper wrapperResponse = new ContentCachingResponseWrapper((HttpServletResponse) response);

        filterChain.doFilter(servletRequest, wrapperResponse);

        byte[] responseBody = wrapperResponse.getContentAsByteArray();
        String responseContent = new String(responseBody, StandardCharsets.UTF_8);

        if (wrapperResponse.getStatus() >= 500) {
            log.info("responseContent = {}", responseContent);
            TelegramService telegramService = applicationContext.getBean(TelegramService.class);
            telegramService.sendMessage();
        }
        wrapperResponse.copyBodyToResponse();
    }
}
