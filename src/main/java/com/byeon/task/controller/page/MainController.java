package com.byeon.task.controller.page;

import com.byeon.task.service.RankingService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class MainController {

    private final RankingService rankingService;
    @GetMapping("/")
    public String mainPage(Model model) {
        List<Object> topFiveRank = rankingService.getTopFiveRank();
        model.addAttribute("ranking", topFiveRank);
        return "main";
    }
}
