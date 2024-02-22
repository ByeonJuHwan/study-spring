package com.byeon.task.controller;

import com.byeon.task.dto.Result;
import com.byeon.task.dto.TranslateDto;
import com.byeon.task.dto.TranslationResponse;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.*;
import org.springframework.stereotype.Controller;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.util.Collections;


@Controller
@RequiredArgsConstructor
@Slf4j
public class TranslateController {

    private final RestTemplate restTemplate;

    @GetMapping("/translate")
    public String translatePage() {
        return "translate";
    }


    //06a07804-585d-46aa-a46e-4c9ca5806c31:fx
    @PostMapping("/translate")
    @ResponseBody
    public String translateResult(@RequestBody TranslateDto dto) throws JsonProcessingException {
        log.info("dto = {}", dto);
        // header 설정
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));

        // api 키 처리
        String apiKey = "06a07804-585d-46aa-a46e-4c9ca5806c31:fx";

        MultiValueMap<String, String> map = new LinkedMultiValueMap<>();
        map.add("auth_key", apiKey);
        map.add("text", dto.getText());
        map.add("target_lang", dto.getTarget_lang());

        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(map, headers);

        String url = "https://api-free.deepl.com/v2/translate";
        ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.POST ,request, String.class);

        return response.getBody();
    }

}


