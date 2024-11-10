package ru.mudan.controller.auth;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import ru.mudan.dto.auth.RegisterUserDTO;
import ru.mudan.services.students.StudentService;
import ru.mudan.services.users.RegistrationService;

@Controller
@RequestMapping("/registration")
@RequiredArgsConstructor
public class AuthController {

    private final RegistrationService registrationService;
    private final StudentService studentService;

    @PostMapping("/admin")
    public String registerAdmin(@Valid RegisterUserDTO registerUserDTO) {
        registrationService.registerAdmin(registerUserDTO);
        return "redirect:/registration/admin";
    }

    @PostMapping("/teacher")
    public String registerTeacher(@Valid RegisterUserDTO registerUserDTO) {
        registrationService.registerTeacher(registerUserDTO);
        return "redirect:/registration/teacher";
    }

    @PostMapping("/parent")
    public String registerParent(@Valid RegisterUserDTO registerUserDTO) {
        registrationService.registerParent(registerUserDTO);
        return "redirect:/registration/parent";
    }

    @PostMapping("/student")
    public String registerStudent(@Valid RegisterUserDTO registerUserDTO) {
        registrationService.registerStudent(registerUserDTO);
        return "redirect:/registration/student";
    }

    @GetMapping("/admin")
    public String getPageForRegisteringAdmin() {
        return "registration/registration-admin";
    }

    @GetMapping("/teacher")
    public String  getPageForRegisteringTeacher() {
        return "registration/registration-teacher";
    }

    @GetMapping("/parent")
    public String  getPageForRegisteringAdminParent(Model model) {
        model.addAttribute("students", studentService.findAllStudentsWithNotParent());
        return "registration/registration-parent";
    }

    @GetMapping("/student")
    public String  getPageForRegisteringAdminStudent() {
        return "registration/registration-student";
    }

}
