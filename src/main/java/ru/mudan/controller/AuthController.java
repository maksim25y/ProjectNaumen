package ru.mudan.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import ru.mudan.dto.RegisterUserDTO;
import ru.mudan.services.users.RegistrationService;

@Controller
@RequestMapping("/registration")
@RequiredArgsConstructor
public class AuthController {

    private final RegistrationService registrationService;

    @PostMapping("/admin")
    public String registerAdmin(RegisterUserDTO registerUserDTO) {
        registrationService.registerAdmin(registerUserDTO);
        return "redirect:/registration/admin";
    }

    @PostMapping("/teacher")
    public String registerTeacher(RegisterUserDTO registerUserDTO) {
        registrationService.registerTeacher(registerUserDTO);
        return "redirect:/registration/teacher";
    }

    @PostMapping("/parent")
    public String registerParent(RegisterUserDTO registerUserDTO) {
        registrationService.registerParent(registerUserDTO);
        return "redirect:/registration/parent";
    }

    @PostMapping("/student")
    public String registerStudent(RegisterUserDTO registerUserDTO) {
        registrationService.registerStudent(registerUserDTO);
        return "redirect:/registration/student";
    }

    @GetMapping("/admin")
    public String registerAdmin() {
        return "registration-admin";
    }

    @GetMapping("/teacher")
    public String registerTeacher() {
        return "registration-teacher";
    }

    @GetMapping("/parent")
    public String registerParent() {
        return "registration-parent";
    }

    @GetMapping("/student")
    public String registerStudent() {
        return "registration-student";
    }

}
