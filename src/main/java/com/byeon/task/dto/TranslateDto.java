package com.byeon.task.dto;


import lombok.Data;

@Data
public class TranslateDto {
    private String text;
    private String target_lang;
}
