package com.byeon.task.controller;

import com.byeon.task.dto.LoginDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
@Slf4j
public class LoginController {

    @GetMapping("/login")
    public String loginPage(Model model) {
        model.addAttribute("login", new LoginDto());
        return "login";
    }

    /**
     * fixme
     * @RestControllerAdvice 를 통해서 Global Exception Handling 방식을 구현하는것을 추천드립니다.
     * 코드가 더 깔끔해지고 전체적으로 API 예외사항 통제가 더 수월해집니다.
     * 그리고 본인만의 예외를 정의해서 사용하시면 됩니다. RuntimeException 을 상속해서 정의해서 사용하면 됩니다.
     * 200 OK, 400 에러, 500 에러 와 401은 인증, 403은 인가 에러로 처리해주시면 됩니다. (이부분 위 내용 해보시고 다시 말씀드릴께요)
     * @param dto
     * @param bindingResult
     * @return
     */
    @PostMapping("/login")
    public String login(@Validated @ModelAttribute("login") LoginDto dto, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            log.error("error = {}", bindingResult.getAllErrors());
            return "/login";
        }


        return "redirect:/";
    }
}
