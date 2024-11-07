package ru.mudan.controller.homeworks;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import ru.mudan.dto.homework.HomeworkDTO;
import ru.mudan.services.classes.ClassService;
import ru.mudan.services.homework.HomeworkService;
import ru.mudan.services.subjects.SubjectService;

@Controller
@RequestMapping("/homeworks")
@SuppressWarnings("MemberName")
@RequiredArgsConstructor
public class HomeworkController {

    private final String HOMEWORK = "homework";
    private final String REDIRECT_CLASSES_ALL = "redirect:/classes/all";
    private final HomeworkService homeworkService;
    private final ClassService classService;
    private final SubjectService subjectService;

    @GetMapping("/all/{classId}")
    public String allHomeworks(Model model,
                               @PathVariable Long classId,
                               @RequestParam Long subjectId) {
        model.addAttribute("homeworks", homeworkService.findAllByClassAndSubject(classId, subjectId));
        return "admin/homeworks/homeworks-index";
    }

    @PostMapping
    public String addHomework(@Valid HomeworkDTO homeworkDTO) {
        homeworkService.save(homeworkDTO);
        return REDIRECT_CLASSES_ALL;
    }

    @GetMapping("/{id}")
    public String getHomeworkById(@PathVariable("id") Long id, Model model) {
        var foundHomework = homeworkService.findById(id);
        model.addAttribute(HOMEWORK, homeworkService.findById(id));
        model.addAttribute("class", classService.findById(foundHomework.classId()));
        model.addAttribute("subject", subjectService.findById(foundHomework.subjectId()));
        return "admin/homeworks/homeworks-show";
    }

    @GetMapping("/{id}/edit")
    public String editHomework(@PathVariable("id") Long id, Model model) {
        model.addAttribute(HOMEWORK, homeworkService.findById(id));
        return "admin/homeworks/homeworks-edit";
    }

    @DeleteMapping("/{id}")
    public String deleteHomework(@PathVariable("id") Long id) {
        homeworkService.delete(id);
        return REDIRECT_CLASSES_ALL;
    }

    @PutMapping("/{id}")
    public String updateHomework(@Valid HomeworkDTO homeworkDTO, @PathVariable("id") Long id) {
        homeworkService.update(id, homeworkDTO);
        return "redirect:/homeworks/" + id;
    }
}
