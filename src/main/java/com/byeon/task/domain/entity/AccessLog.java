package com.byeon.task.domain.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
public class AccessLog {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String ipAddress;
    private String userAgent;
    private LocalDateTime requestTime;
    private String method;
    private String uri;

    //  String requestBody; 추가필요
    private String requestBody;
    //  String responseBody; 추가필요
    private String responseBody;
    //  int elapseTime; 추가필요 : 얼마나걸렸느냐
    private Integer elapseTime;
}
