package com.byeon.task.controller;

import com.byeon.task.consumers.MessageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequiredArgsConstructor
public class RabbitMQController {

    private final MessageService messageService;

    // fixme 캐멀케이스는 지양하는게 좋을 것 같아요. 보통 대쉬(-) 를 통해서 연결히는게 좋습니다. 언더바 _ 보다 더 선호됩니다.
    @GetMapping("/sendMQ")
    public ResponseEntity<String> sendMQMessage() {
        return ResponseEntity.ok("Message sent to MQ");
    }
}
