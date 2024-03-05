package com.byeon.task.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@Builder
public class TelegramDeadMessageDto {

    private String requestMethod;
    private String uri;
    private String errorMessage;
}
