package com.byeon.task.controller;

import com.byeon.task.dto.LoginDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@Slf4j
@RequiredArgsConstructor
public class LoginController {

    @GetMapping("/login")
    public String loginPage(@RequestParam(required = false) String error, Model model) {
        model.addAttribute("login", new LoginDto());
        model.addAttribute("error", error); // todo 에러 메시지를 화면에 보여주기.
        return "login";
    }

    /**
     * fixme 이 부분은 제가 잘못 전달드린것 같네요. 저희는 폼로그인을 활용하기 때문에 스프링 시큐리티 기본 설정으로 가능할것 같아서 이부분은 필요가 없을것 같습니다.
     * fixme SecurityConfig 에서 처리하도록 하면 될 것 같습니다.
     * fixme 아래 말씀드린 "@RestControllerAdvice" 관련 내용은 다른 API 에서 활용하셔야할것 같아요.
     * @RestControllerAdvice 를 통해서 Global Exception Handling 방식을 구현하는것을 추천드립니다.
     * 코드가 더 깔끔해지고 전체적으로 API 예외사항 통제가 더 수월해집니다.
     * 그리고 본인만의 예외를 정의해서 사용하시면 됩니다. RuntimeException 을 상속해서 정의해서 사용하면 됩니다.
     * 200 OK, 400 에러, 500 에러 와 401은 인증, 403은 인가 에러로 처리해주시면 됩니다. (이부분 위 내용 해보시고 다시 말씀드릴께요)
     * @param dto
     * @param bindingResult
     * @return
     */
//    @PostMapping("/login")
//    public String login(@Validated @ModelAttribute("login") LoginDto dto, BindingResult bindingResult) {
//        if (bindingResult.hasErrors()) {
//            log.error("error = {}", bindingResult.getAllErrors());
//            return "/login";
//        }
//        return "redirect:/";
//    }
}
