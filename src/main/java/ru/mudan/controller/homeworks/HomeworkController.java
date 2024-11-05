package ru.mudan.controller.homeworks;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import ru.mudan.domain.entity.Homework;
import ru.mudan.dto.HomeworkDTO;
import ru.mudan.services.classes.ClassService;
import ru.mudan.services.homework.HomeworkService;
import ru.mudan.services.subjects.SubjectService;

@Controller
@RequestMapping("/homeworks")
@RequiredArgsConstructor
public class HomeworkController {

    private final HomeworkService homeworkService;
    private final ClassService classService;
    private final SubjectService subjectService;

    @GetMapping("/all/{classId}")
    public String allHomeworks(Model model,
                               @PathVariable Long classId,
                               @RequestParam Long subjectId) {
        model.addAttribute("homeworks", homeworkService.findAllByClassAndSubject(classId, subjectId));
        return "admin/homeworks/homeworks-show";
    }

    @PostMapping
    public String addHomework(HomeworkDTO homeworkDTO) {
        homeworkService.save(homeworkDTO);
        return "redirect:/classes/all";
    }
}
