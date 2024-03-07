package com.byeon.task.controller.api;

import com.byeon.task.common.RestResult;
import com.byeon.task.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import org.thymeleaf.spring6.context.SpringContextUtils;

@RestController
@RequiredArgsConstructor
public class RedisObjectController {

    private final MemberService memberService;

    @GetMapping("/member/{userId}/profile")
    public RestResult getMemberProfile(@PathVariable String userId) {
        return memberService.getUserProfile(userId);
    }
}
