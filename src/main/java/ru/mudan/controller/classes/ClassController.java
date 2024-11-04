package ru.mudan.controller.classes;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import ru.mudan.dto.classes.ClassDTO;
import ru.mudan.services.classes.ClassService;

@Controller
@RequestMapping("/classes")
@SuppressWarnings({"MemberName", "MultipleStringLiterals"})
@RequiredArgsConstructor
public class ClassController {

    private final String REDIRECT_CLASSES_ALL = "redirect:/classes/all";
    private final ClassService classService;

    @GetMapping("/all")
    public String getAllClasses(Model model) {
        model.addAttribute("classes", classService.findAll());
        return "admin/classes/classes-index";
    }

    @GetMapping("/{id}")
    public String getClassInfo(Model model, @PathVariable Long id) {
        var classDTO = classService.findById(id);
        model.addAttribute("cl", classService.findById(id));
        model.addAttribute("students", classService.findAllStudentsForClass(classDTO));
        return "admin/classes/classes-show";
    }

    @GetMapping("/add")
    public String createClass(Model model) {
        model.addAttribute("students", classService.findStudentsWithNotClass());
        return "admin/classes/classes-add";
    }

    @PostMapping
    public String createClass(ClassDTO classDTO) {
        classService.save(classDTO);
        return REDIRECT_CLASSES_ALL;
    }

    @GetMapping("/{id}/edit")
    public String editClass(@PathVariable Long id, Model model) {
        model.addAttribute("cl", classService.findById(id));
        return "admin/classes/classes-edit";
    }

    @PutMapping("/{id}")
    public String updateClass(@PathVariable Long id, ClassDTO classDTO) {
        classService.update(classDTO, id);
        return "redirect:/classes/" + id;
    }

    @DeleteMapping("/{id}")
    public String deleteClass(@PathVariable Long id) {
        classService.deleteById(id);
        return REDIRECT_CLASSES_ALL;
    }
}
