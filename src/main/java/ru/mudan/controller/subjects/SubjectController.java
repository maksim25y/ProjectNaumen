package ru.mudan.controller.subjects;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import ru.mudan.services.subjects.SubjectService;

@Controller
@RequestMapping("/subjects")
@RequiredArgsConstructor
public class SubjectController {

    private final SubjectService subjectService;

    @GetMapping("/all")
    public String all(Model model) {
        model.addAttribute("subjects", subjectService.findAll());
        return "admin/subjects/subjects-index";
    }

    @GetMapping("/{id}")
    public String getSubjectById(Model model, @PathVariable Long id) {
        model.addAttribute("subject", subjectService.findById(id));
        return "admin/subjects/subjects-show";
    }
}
