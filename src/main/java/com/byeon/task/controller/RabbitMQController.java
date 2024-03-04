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


    @GetMapping("/sendMQ")
    public ResponseEntity<String> sendMQMessage() {
        return ResponseEntity.ok("Message sent to MQ");
    }
}
