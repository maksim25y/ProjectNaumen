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

    @GetMapping("/admin")
    public String registerAdmin() {
        return "registration-admin";
    }

}
