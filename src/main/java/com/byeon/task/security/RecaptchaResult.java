package com.byeon.task.security;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
 @NoArgsConstructor
 class RecaptchaResult {
    private boolean success;

    @JsonProperty("challenge_ts")
    private String challengeTs;

    private String hostname;
 }