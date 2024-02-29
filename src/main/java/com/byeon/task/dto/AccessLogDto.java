package com.byeon.task.dto;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class AccessLogDto {

    private String ipAddress;
    private String userAgent;
    private LocalDateTime requestTime;
    private String requestMethod;
    private String uri;
}
