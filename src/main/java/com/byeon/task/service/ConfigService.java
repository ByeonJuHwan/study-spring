package com.byeon.task.service;

import com.byeon.task.domain.entity.Config;
import com.byeon.task.repository.ConfigRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Slf4j
@Service
@RequiredArgsConstructor
public class ConfigService {

    private final ConfigRepository configRepository;
    private final StringRedisTemplate redisTemplate;

    @Cacheable(value = "configCache", key = "#valueName")
    public String getConfElapseTime(String valueName) {
        Config config = configRepository.findConfigByConfigName(valueName).orElseThrow(() -> new RuntimeException("설정값이 없습니다."));
        return config.getConfigValue();
    }

    public String getConfElapseTimeWithRedis(String valueName) {
        ValueOperations<String, String> ops = redisTemplate.opsForValue();
        String confValue = ops.get(valueName);
        if (confValue == null) {
            Config config = configRepository.findConfigByConfigName(valueName).orElseThrow(() -> new IllegalArgumentException("설정값이 없습니다."));
            setConfValueOnRedis(valueName,config.getConfigValue());
            return config.getConfigValue();
        }
        return confValue;
    }

    private void setConfValueOnRedis(String valueName, String configValue) {
        redisTemplate.opsForValue().set(valueName,configValue, 60, TimeUnit.MINUTES);
    }
}
