package ru.mudan.controller.subjects;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import ru.mudan.dto.subjects.SubjectCreateDTO;
import ru.mudan.dto.subjects.SubjectUpdateDTO;
import ru.mudan.services.classes.ClassService;
import ru.mudan.services.subjects.SubjectService;

@Controller
@SuppressWarnings({"MultipleStringLiterals", "MemberName"})
@RequestMapping("/subjects")
@RequiredArgsConstructor
public class SubjectController {

    private final String REDIRECT_SUBJECTS_ALL = "redirect:/subjects/all";
    private final SubjectService subjectService;
    private final ClassService classService;

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

    @GetMapping("/add")
    public String createSubject(Model model) {
        model.addAttribute("classes", classService.findAll());
        return "admin/subjects/subjects-add";
    }

    @PostMapping
    public String save(@Valid SubjectCreateDTO subjectDTO) {
        subjectService.save(subjectDTO);
        return REDIRECT_SUBJECTS_ALL;
    }

    @GetMapping("/{id}/edit")
    public String createSubject(Model model, @PathVariable Long id) {
        model.addAttribute("subject", subjectService.findById(id));
        return "admin/subjects/subjects-edit";
    }

    @PutMapping("/{id}")
    public String update(@Valid SubjectUpdateDTO subjectDTO, @PathVariable Long id) {
        subjectService.update(subjectDTO, id);
        return "redirect:/subjects/" + id;
    }

    @DeleteMapping("/{id}")
    public String delete(@PathVariable Long id) {
        subjectService.deleteById(id);
        return REDIRECT_SUBJECTS_ALL;
    }
}
