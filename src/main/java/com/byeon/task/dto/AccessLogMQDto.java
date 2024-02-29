package com.byeon.task.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AccessLogMQDto {
    private String userId;
    private String ipAddress;
    private String userAgent;
    private LocalDateTime requestTime;
    private String requestMethod;
    private String uri;
    private String requestBody;
    private String responseBody;
    private Double elapseTime;
}
