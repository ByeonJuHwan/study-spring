package com.byeon.task.config;

import com.byeon.task.config.filter.AccessLogFilter;
import com.byeon.task.repository.AccessLogRepository;
import jakarta.servlet.Filter;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

import java.util.Collections;

@Configuration
@RequiredArgsConstructor
public class AppConfig {

    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }

    @Bean
    public ModelMapper modelMapper() {
        return new ModelMapper();
    }

    private final AccessLogRepository accessLogRepository;
    @Bean
    public FilterRegistrationBean<Filter> logFilter() {
        FilterRegistrationBean<Filter> filterFilterRegistrationBean = new FilterRegistrationBean<>();
        filterFilterRegistrationBean.setFilter(new AccessLogFilter(accessLogRepository));
        filterFilterRegistrationBean.setOrder(1);
        filterFilterRegistrationBean.setUrlPatterns(Collections.singleton("/*"));
        return filterFilterRegistrationBean;
    }
}
