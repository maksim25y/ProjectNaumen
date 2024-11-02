package ru.mudan.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import ru.mudan.services.classes.ClassService;

@Controller
@RequestMapping("/classes")
@RequiredArgsConstructor
public class ClassController {

    private final ClassService classService;

    @GetMapping("/all")
    public String all(Model model) {
        model.addAttribute("classes", classService.findAllClasses());
        return "admin/classes-index";
    }
}
