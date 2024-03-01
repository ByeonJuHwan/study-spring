package com.byeon.task.common;

import lombok.RequiredArgsConstructor;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Component;

import java.util.Locale;

@Component
@RequiredArgsConstructor
public class CommonMessage {

    private final MessageSource messageSource;
    public String getTimeoutForElapsedTime() {
        return messageSource.getMessage("timeout.elapseTime", null, Locale.getDefault());
    }
}
