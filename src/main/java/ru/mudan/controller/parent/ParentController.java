package ru.mudan.controller.parent;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import ru.mudan.services.parent.ParentService;
import ru.mudan.services.students.StudentService;

/**
 * Контроллер, принимающий запросы
 * только от родителя
 */
@Controller
@RequestMapping("/parent")
@RequiredArgsConstructor
public class ParentController {

    private final ParentService parentService;
    private final StudentService studentService;

    /**
     * Эндпоинт для получения шаблона аккаунта родителя
     */
    @GetMapping("/account")
    public String account(Model model, Authentication authentication) {
        var parent = parentService.findParentByAuth(authentication);
        model.addAttribute("parent", parent);
        model.addAttribute("students", studentService.getAllStudentsForParent(parent.id()));
        return "parent/parent-main-page";
    }
}
