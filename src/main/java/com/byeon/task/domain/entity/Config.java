package com.byeon.task.domain.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.*;

import static lombok.AccessLevel.*;

@Getter
@Setter
@Entity
@ToString
@NoArgsConstructor(access = PROTECTED)
public class Config {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String configName;

    private String configValue;

    public Config(String configName, String configValue) {
        this.configName = configName;
        this.configValue = configValue;
    }
}
