package ru.mudan.controller.auth;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import ru.mudan.dto.auth.RegisterUserDTO;
import ru.mudan.services.users.RegistrationService;

@Controller
@RequestMapping("/registration")
@RequiredArgsConstructor
public class AuthController {

    private final RegistrationService registrationService;

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
    public String registerAdmin() {
        return "registration/registration-admin";
    }

    @GetMapping("/teacher")
    public String registerTeacher() {
        return "registration/registration-teacher";
    }

    @GetMapping("/parent")
    public String registerParent() {
        return "registration/registration-parent";
    }

    @GetMapping("/student")
    public String registerStudent() {
        return "registration/registration-student";
    }

}
