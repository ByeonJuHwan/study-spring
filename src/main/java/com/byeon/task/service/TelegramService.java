package com.byeon.task.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
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

    public void sendMessage() {
        log.error("exception message start...");
        String telegramUrl = "https://api.telegram.org/bot" + telegramKey + "/sendMessage";

        // Header json 방식으로
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        String exceptionMessage = "code : " + HttpStatus.INTERNAL_SERVER_ERROR.toString() + " message : 서버에러입니다.";

        String message = "{\"chat_id\":\"" + chatId + "\", \"text\":\"" + exceptionMessage + "\"}";

        HttpEntity<String> request = new HttpEntity<>(message, headers);

        String response = restTemplate.postForObject(telegramUrl, request, String.class);

        log.info("response = {}", response);
    }
}
