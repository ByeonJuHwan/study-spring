package com.byeon.task.repository;

import com.byeon.task.domain.entity.Config;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ConfigRepository extends JpaRepository<Config, Long> {
    Optional<Config> findConfigByConfigName(String configName);
}
