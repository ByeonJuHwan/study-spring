package com.byeon.task.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

@RestController
public class TelegramExceptionController {

    @GetMapping("/ex")
    public void triggerException() throws Exception {
        throw new Exception(("테스트"));
    }

    @GetMapping("/timeout")
    public void timeOut() throws InterruptedException {
        Thread.sleep(11000);
    }
}
