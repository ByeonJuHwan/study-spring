package com.byeon.task.controller;

import com.byeon.task.dto.TranslateDto;
import com.byeon.task.service.TranslateService;
import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.*;
import org.springframework.stereotype.Controller;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.client.RestTemplate;

import java.util.Collections;


@Controller
@RequiredArgsConstructor
@Slf4j
public class TranslateController {


    private final TranslateService translateService;

    @GetMapping("/translate")
    public String translatePage() {
        return "translate";
    }


    //06a07804-585d-46aa-a46e-4c9ca5806c31:fx
    @PostMapping("/translate")
    @ResponseBody
    public String translateResult(@RequestBody TranslateDto dto) throws JsonProcessingException {
        log.info("dto = {}", dto);
        // fixme 별도의 서비스를 만들어서 call 할수있도록 해주세요.
        // header 설정
        ResponseEntity<String> response = translateService.callApiResult(dto);
        return response.getBody();
    }
}


