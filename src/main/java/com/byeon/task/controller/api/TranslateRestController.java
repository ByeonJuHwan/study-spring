package com.byeon.task.controller.api;

import com.byeon.task.dto.TranslateDto;
import com.byeon.task.service.TranslateService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@Slf4j
public class TranslateRestController {

    private final TranslateService translateService;
    @PostMapping("/translate/data")
    public String translateResult(@RequestBody TranslateDto dto) {
        log.info("dto = {}", dto);
        // 별도의 서비스를 만들어서 call 할수있도록 해주세요.
        // header 설정
        ResponseEntity<String> response = translateService.callApiResult(dto);
        return response.getBody();
    }
}
