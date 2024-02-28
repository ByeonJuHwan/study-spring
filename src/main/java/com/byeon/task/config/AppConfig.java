package com.byeon.task.config;

import com.byeon.task.config.filter.AccessLogFilter;
import com.byeon.task.config.filter.ExceptionCashingFilter;
import com.byeon.task.repository.AccessLogRepository;
import com.byeon.task.service.TelegramService;
import jakarta.servlet.Filter;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

import java.util.Collections;
import java.util.List;

@Configuration
@RequiredArgsConstructor
public class AppConfig {

    private final AccessLogRepository accessLogRepository;

    @Bean
    public FilterRegistrationBean<Filter> accessLogFilter() {
        FilterRegistrationBean<Filter> filterFilterRegistrationBean = new FilterRegistrationBean<>();
        filterFilterRegistrationBean.setFilter(new AccessLogFilter(accessLogRepository));
        filterFilterRegistrationBean.setOrder(1);
        filterFilterRegistrationBean.setUrlPatterns(List.of("/translate/data"));
        return filterFilterRegistrationBean;
    }

//    @Bean
//    public FilterRegistrationBean<Filter> telegramFilter(ApplicationContext applicationContext) {
//        FilterRegistrationBean<Filter> filterFilterRegistrationBean = new FilterRegistrationBean<>();
//        filterFilterRegistrationBean.setFilter(new ExceptionCashingFilter(telegramService));
//        filterFilterRegistrationBean.setOrder(2);
//        filterFilterRegistrationBean.setUrlPatterns(Collections.singleton("/*"));
//        return filterFilterRegistrationBean;
//    }
}
