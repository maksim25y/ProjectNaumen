package ru.mudan.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import ru.mudan.dto.ClassDTO;
import ru.mudan.services.classes.ClassService;

@Controller
@RequestMapping("/classes")
@RequiredArgsConstructor
public class ClassController {

    private final ClassService classService;

    @GetMapping("/all")
    public String all(Model model) {
        model.addAttribute("classes", classService.findAll());
        return "admin/classes/classes-index";
    }

    @GetMapping("/{id}")
    public String getClassInfo(Model model, @PathVariable Long id) {
        model.addAttribute("cl", classService.findById(id));
        return "admin/classes/classes-show";
    }

    @GetMapping("/add")
    public String createClass() {
        return "admin/classes/classes-add";
    }

    @PostMapping("/add")
    public String createClass(ClassDTO classDTO) {
        classService.save(classDTO);
        return "redirect:/classes/all";
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
        return "redirect:/classes/all";
    }
}
