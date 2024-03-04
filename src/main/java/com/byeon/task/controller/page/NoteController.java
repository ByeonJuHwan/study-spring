package com.byeon.task.controller.page;

import com.byeon.task.domain.entity.Note;
import com.byeon.task.dto.NoteSearchDto;
import com.byeon.task.service.NoteService;
import com.byeon.task.service.front.NoteFrontService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

//todo 우선 여기에 적을께요. 저는 페이지를 나타내는 @Controller 와 API 를 리턴하는 @RestController 가 서로 다른 일을 한다고 봐서요.
// 각각 page, api 라는 패키지를 사용하는것을 즐겨합니다. 물론 @Controller 안에 뷰와 API 를 리턴하는 경우가 있을 수 있는데요. 그때는 저는 page 라고 이름을 짓긴해요.
// 그리고 클래스 이름도 NotePage, NoteApi 이런식으로요.
// 이건 호불호가 있긴한데요 참고해주세요.
@Slf4j
@Controller
@RequiredArgsConstructor
public class NoteController {

    private final NoteFrontService noteFrontService;

    @GetMapping("/notes")
    public String myNote(Model model) {
        List<NoteSearchDto> notes = noteFrontService.noteToSearchDto();
        model.addAttribute("notes", notes);
        return "notes";
    }
}
