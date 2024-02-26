package com.byeon.task.exception;


import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(RuntimeException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)   // 400
    public Object runtimeException(RuntimeException e) {

        //
        return null;
    }

    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)   // 500
    public Object exception(Exception e) {
//        e.getMessage();
        // todo Telegram 으로 전송 !!!!   429 에러...
        // todo Rate Limit 을 이해하자.... 서버를 보호하는 방법이면서, 라이선스 정책과 관련 있다. (API 사용에 대해서 유료화 가능...)
        return null;
    }
}
