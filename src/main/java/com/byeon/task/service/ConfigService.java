package com.byeon.task.service;

import com.byeon.task.domain.entity.Config;
import com.byeon.task.repository.ConfigRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class ConfigService {

    private final ConfigRepository configRepository;

    @Cacheable(value = "configCache", key = "#valueName")
    public String getConfElapseTime(String valueName) {
        Config config = configRepository.findConfigByConfigName(valueName).orElseThrow(() -> new RuntimeException("설정값이 없습니다."));
        return config.getConfigValue();
    }
}
