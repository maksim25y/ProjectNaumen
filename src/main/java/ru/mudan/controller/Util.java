package ru.mudan.controller;

import jakarta.servlet.http.HttpServletRequest;
import lombok.experimental.UtilityClass;

/**
 * Утилитный класс для контроллеров
 */
@UtilityClass
public class Util {

    /**
     * Метод для редиректа на предыдущий адрес
     *
     * @param request - текущий http request
     */
    public static String doRedirect(HttpServletRequest request) {
        var referer = request.getHeader("Referer");
        return "redirect:" + ((referer != null) ? referer : "/");
    }
}
