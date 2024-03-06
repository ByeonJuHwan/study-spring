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

/**
 * 객체를 json 형태로 레디스에 넣어보는것도 해보면 좋을 것 같네요.
 * 그럼 객체를 직렬화, 역직렬화해서 사용하는 일에도 쓰일수 있음을 알고 나면 다양한 곳에서도 사용할 수 있습니다.
 * 그리고 Cacheable 통해서 캐시에 들어가게 되면 캐시에 어떤식으로 들어가는지를 확인해보면서 학습을 해주시면 더 좋을 것 같아요.
 */
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
