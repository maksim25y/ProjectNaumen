package ru.mudan.controller.auth;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import ru.mudan.dto.auth.RegisterUserDTO;
import ru.mudan.services.students.StudentService;
import ru.mudan.services.users.RegistrationService;
import static ru.mudan.controller.Util.doRedirect;

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
    public String registerAdmin(@Valid RegisterUserDTO registerUserDTO, HttpServletRequest request) {
        registrationService.registerAdmin(registerUserDTO);
        return doRedirect(request);
    }

    /**
     * Эндпоинт для регистрации учителя
     *
     * @param registerUserDTO - входные данные для регистрации
     */
    @PostMapping("/teacher")
    public String registerTeacher(@Valid RegisterUserDTO registerUserDTO, HttpServletRequest request) {
        registrationService.registerTeacher(registerUserDTO);
        return doRedirect(request);
    }

    /**
     * Эндпоинт для регистрации родителя
     *
     * @param registerUserDTO - входные данные для регистрации
     */
    @PostMapping("/parent")
    public String registerParent(@Valid RegisterUserDTO registerUserDTO, HttpServletRequest request) {
        registrationService.registerParent(registerUserDTO);
        return doRedirect(request);
    }

    /**
     * Эндпоинт для регистрации ученика
     *
     * @param registerUserDTO - входные данные для регистрации
     */
    @PostMapping("/student")
    public String registerStudent(@Valid RegisterUserDTO registerUserDTO, HttpServletRequest request) {
        registrationService.registerStudent(registerUserDTO);
        return doRedirect(request);
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
