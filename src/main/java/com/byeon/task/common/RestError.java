package com.byeon.task.common;

import lombok.Data;

@Data
public class RestError {

    private String code;
    private String message;

    public RestError(String code, String message) {
        this.code = code;
        this.message = message;
    }
}
