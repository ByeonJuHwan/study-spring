package com.byeon.task.service;


import com.byeon.task.dto.TranslateDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.util.Collections;

@Service
@RequiredArgsConstructor
@Slf4j
public class TranslateService {

    private final RestTemplate restTemplate;

    @Value("${deepl.api-key}")
    private String apiKey;

    @Value("${deepl.url}")
    private String url;

    public ResponseEntity<String> callApiResult(TranslateDto dto) {

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));

        // fixme yml 설정으로 빼주세요.
        // api 키 처리
        //String apiKey = "06a07804-585d-46aa-a46e-4c9ca5806c31:fx";

        MultiValueMap<String, String> map = new LinkedMultiValueMap<>();
        map.add("auth_key", apiKey);
        map.add("text", dto.getText());
        map.add("target_lang", dto.getTargetLang());

        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(map, headers);

        // fixme yml 설정으로 빼주세요.
        //String url = "https://api-free.deepl.com/v2/translate";
        return restTemplate.exchange(url, HttpMethod.POST ,request, String.class);
    }
}
