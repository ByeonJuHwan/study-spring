package com.byeon.task.dto;


import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

@Data
public class MemberJoinDto {

    @NotBlank
    @Length(min = 2, max = 13)
    private String userId;

    @NotBlank
    @Length(min = 2, max = 13)
    private String password;

    @NotBlank
    private String recaptcha;
}
