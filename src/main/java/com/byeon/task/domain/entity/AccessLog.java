package com.byeon.task.domain.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;
import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
public class AccessLog {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String userId;
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
    //private Integer elapseTime;

    // 마이크로초까지 저장하기 위해서 변경
    private Double elapseTime;
}
