package com.byeon.task.controller;

import com.byeon.task.dto.MemberJoinDto;
import com.byeon.task.service.front.MemberFrontService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
@RequiredArgsConstructor
@Slf4j
public class MemberController {

    private final MemberFrontService memberFrontService;

    @GetMapping("/join")
    public String join(Model model) {
        model.addAttribute("member",new MemberJoinDto());
        return "join";
    }

    @PostMapping("/join")
    public String join(@Validated @ModelAttribute("member") MemberJoinDto memberJoinDto, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            log.error("error = {}", bindingResult.getAllErrors());
            return "/join";
        }

        log.info("memberJoinDto = {}", memberJoinDto);
        memberFrontService.join(memberJoinDto);

        return "redirect:/";
    }
}
