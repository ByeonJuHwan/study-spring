package com.byeon.task.config.filter;

import com.byeon.task.service.threadlocal.ThreadLocalSaveUserID;
import jakarta.servlet.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.io.IOException;

@Slf4j
@RequiredArgsConstructor
public class CheckLoginUserFilter implements Filter {

    private final ThreadLocalSaveUserID saveUserID;
    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated()) {
            saveUserID.saveUserId(authentication.getName());
        }
        log.info("saveUserID = {}", saveUserID.getUserId());
        filterChain.doFilter(servletRequest, servletResponse);
    }
}
