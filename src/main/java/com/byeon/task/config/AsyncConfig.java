package com.byeon.task.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;

@EnableAsync
@Configuration
public class AsyncConfig {

    @Bean
    public Executor executor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        // 임의로 저장
        executor.setCorePoolSize(2);
        executor.setMaxPoolSize(5);
        executor.setQueueCapacity(100);
        executor.setThreadNamePrefix("telegram - ");
        executor.initialize();
        return executor;
    }
}
