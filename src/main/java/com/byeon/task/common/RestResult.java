package com.byeon.task.common;

import lombok.Data;

@Data
public class RestResult {

    private Object data;
    public RestResult(Object data) {
        this.data = data;
    }

}
