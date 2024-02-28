package com.byeon.task.config;

import com.byeon.task.service.threadlocal.ThreadLocalSaveUserID;
import com.byeon.task.service.threadlocal.ThreadLocalUserIdService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

@Configuration
@RequiredArgsConstructor
public class WebConfig {

    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }

    @Bean
    public ModelMapper modelMapper() {
        return new ModelMapper();
    }

    @Bean
    public ThreadLocalSaveUserID threadLocalSaveUserID() {
        return new ThreadLocalUserIdService();
    }

}
