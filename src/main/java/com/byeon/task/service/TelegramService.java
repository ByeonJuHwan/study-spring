package com.byeon.task.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Slf4j
@Service
@RequiredArgsConstructor
public class TelegramService {
    private final RestTemplate restTemplate;

    @Value("${telegram.key}")
    private String telegramKey;

    @Value("${telegram.chatId}")
    private String chatId;


    // fixme 이 지점을 @Async 로 처리해야 합니다. Telegram 에서 429 에러 등의 에러 상황이 발생하더라도 본 서비스에는 영향을 주지 말아야 합니다.
    @Async
    public void sendMessage(HttpStatus status, String caused) {
        log.error("message start...");
        sendToTelegram(status, caused);
    }

    /**
     * Http Status, 에러메세지를 인자로 받음으로서 어디서든 사용가능하게 변경
     * @param status HttpStatus :) 500, 400 ...
     * @param caused 텔레그램으로 보낼 에러 원인 메세지
     */
    private void sendToTelegram(HttpStatus status, String caused) {
        String telegramUrl = "https://api.telegram.org/bot" + telegramKey + "/sendMessage";

        // Header json 방식으로
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        String exceptionMessage = "code : " + status.toString() + System.lineSeparator() +"message : " + caused;

        String message = "{\"chat_id\":\"" + chatId + "\", \"text\":\"" + exceptionMessage + "\"}";

        HttpEntity<String> request = new HttpEntity<>(message, headers);

        String response = restTemplate.postForObject(telegramUrl, request, String.class);

        log.info("response = {}", response);
    }
}
