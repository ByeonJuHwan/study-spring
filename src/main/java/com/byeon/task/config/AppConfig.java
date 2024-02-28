package com.byeon.task.config;

import com.byeon.task.config.filter.AccessLogFilter;
import com.byeon.task.config.filter.CheckLoginUserFilter;
import com.byeon.task.repository.AccessLogRepository;
import com.byeon.task.service.threadlocal.ThreadLocalSaveUserID;
import jakarta.servlet.Filter;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
@RequiredArgsConstructor
public class AppConfig {

    private final AccessLogRepository accessLogRepository;
    private final ThreadLocalSaveUserID threadLocalSaveUserID;

    @Bean
    public FilterRegistrationBean<Filter> accessLogFilter() {
        FilterRegistrationBean<Filter> filterFilterRegistrationBean = new FilterRegistrationBean<>();
        filterFilterRegistrationBean.setFilter(new AccessLogFilter(accessLogRepository,threadLocalSaveUserID));
        filterFilterRegistrationBean.setOrder(2);
        filterFilterRegistrationBean.setUrlPatterns(List.of("/translate/data"));
        return filterFilterRegistrationBean;
    }

    @Bean
    public FilterRegistrationBean<Filter> checkLoginUserFilter() {
        FilterRegistrationBean<Filter> filterFilterRegistrationBean = new FilterRegistrationBean<>();
        filterFilterRegistrationBean.setFilter(new CheckLoginUserFilter(threadLocalSaveUserID));
        filterFilterRegistrationBean.setOrder(1);
        filterFilterRegistrationBean.setUrlPatterns(List.of("/translate/data"));
        return filterFilterRegistrationBean;
    }
}
