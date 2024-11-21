package ru.mudan.controller.auth;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import ru.mudan.dto.auth.RegisterUserDTO;
import ru.mudan.services.students.StudentService;
import ru.mudan.services.users.RegistrationService;

/**
 * Контроллер, принимающий запросы на
 * регистрацию новых пользователей
 */
@Controller
@RequestMapping("/registration")
@RequiredArgsConstructor
public class RegistrationController {

    private final RegistrationService registrationService;
    private final StudentService studentService;

    /**
     * Эндпоинт для регистрации администратора
     *
     * @param registerUserDTO - входные данные для регистрации
     */
    @PostMapping("/admin")
    public String registerAdmin(@Valid RegisterUserDTO registerUserDTO) {
        registrationService.registerAdmin(registerUserDTO);
        return "redirect:/registration/admin";
    }

    /**
     * Эндпоинт для регистрации учителя
     *
     * @param registerUserDTO - входные данные для регистрации
     */
    @PostMapping("/teacher")
    public String registerTeacher(@Valid RegisterUserDTO registerUserDTO) {
        registrationService.registerTeacher(registerUserDTO);
        return "redirect:/registration/teacher";
    }

    /**
     * Эндпоинт для регистрации родителя
     *
     * @param registerUserDTO - входные данные для регистрации
     */
    @PostMapping("/parent")
    public String registerParent(@Valid RegisterUserDTO registerUserDTO) {
        registrationService.registerParent(registerUserDTO);
        return "redirect:/registration/parent";
    }

    /**
     * Эндпоинт для регистрации ученика
     *
     * @param registerUserDTO - входные данные для регистрации
     */
    @PostMapping("/student")
    public String registerStudent(@Valid RegisterUserDTO registerUserDTO) {
        registrationService.registerStudent(registerUserDTO);
        return "redirect:/registration/student";
    }

    /**
     * Эндпоинт для получения шаблона регистрации администратора
     */
    @GetMapping("/admin")
    public String getPageForRegisteringAdmin() {
        return "registration/registration-admin";
    }

    /**
     * Эндпоинт для получения шаблона регистрации учителя
     */
    @GetMapping("/teacher")
    public String getPageForRegisteringTeacher() {
        return "registration/registration-teacher";
    }

    /**
     * Эндпоинт для получения шаблона регистрации родителя
     */
    @GetMapping("/parent")
    public String getPageForRegisteringAdminParent(Model model) {
        model.addAttribute("students", studentService.findAllStudentsWithNotParent());
        return "registration/registration-parent";
    }

    /**
     * Эндпоинт для получения шаблона регистрации ученика
     */
    @GetMapping("/student")
    public String getPageForRegisteringAdminStudent() {
        return "registration/registration-student";
    }

}
