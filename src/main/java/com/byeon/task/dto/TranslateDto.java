package com.byeon.task.dto;


import lombok.Data;

@Data
public class TranslateDto {
    private String text;

    // fixme 자바는 스네이크 케이스보다는 캐멀 케이스 스타일로 해주시는게 좋습니다.
    private String target_lang;
}
