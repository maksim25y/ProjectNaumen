package ru.mudan.controller.homeworks;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import ru.mudan.services.homework.HomeworkService;

@Controller
@RequestMapping("/homeworks")
@RequiredArgsConstructor
public class HomeworkController {

    private final HomeworkService homeworkService;

    @GetMapping("/all/{classId}")
    public String allHomeworks(Model model,
                               @PathVariable Long classId,
                               @RequestParam Long subjectId) {
        model.addAttribute("homeworks", homeworkService.findAllByClassAndSubject(classId, subjectId));
        return "admin/homeworks/homeworks-show";
    }
}
