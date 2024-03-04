package com.byeon.task.controller;

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
