package ru.mudan.controller.auth;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * Контроллер, принимающий запросы
 * на вход в аккаунт
 */
@Controller
@RequestMapping("/login")
public class LoginController {

    /**
     * Эндпоинт для получения шаблона для входа в аккаунт
     */
    @GetMapping
    public String login() {
        return "login";
    }
}
