package ru.mudan.controller.student;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import ru.mudan.services.students.StudentService;
import ru.mudan.services.subjects.SubjectService;

/**
 * Контроллер, принимающий запросы
 * только от ученика
 */
@Controller
@RequestMapping("/student")
@RequiredArgsConstructor
public class StudentController {

    private final StudentService studentService;
    private final SubjectService subjectService;

    /**
     * Эндпоинт для получения шаблона аккаунта ученика
     */
    @GetMapping("/account")
    public String account(Model model, Authentication authentication) {
        var student = studentService.findStudentByAuth(authentication);
        model.addAttribute("student", student);
        if (student.classId() != null) {
            model.addAttribute("subjects", subjectService.findAllSubjectsForClass(student.classId()));
        }
        return "student/student-main-page";
    }
}
