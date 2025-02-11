package ru.mudan.controller;

import jakarta.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.ui.Model;
import org.springframework.validation.BindException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import ru.mudan.exceptions.base.ApplicationForbiddenException;
import ru.mudan.exceptions.base.ApplicationRuntimeException;

/**
 * Класс для обработки ошибок
 * во время работы приложения
 */
@ControllerAdvice
@RequiredArgsConstructor
@SuppressWarnings({"MultipleStringLiterals", "MemberName"})
public class ExceptionHandlerController {

    @Value("${attribute.error}")
    private String nameOfAttributeForErrors;
    private final MessageSource messageSource;

    @ExceptionHandler(BindException.class)
    public String handleBindException(BindException exception,
                                      RedirectAttributes redirectAttributes,
                                      Model model,
                                      Locale locale,
                                      HttpServletRequest request) {

        List<String> errors = exception.getBindingResult().getFieldErrors()
                .stream()
                .map(fieldError -> messageSource.getMessage(fieldError, locale))
                .collect(Collectors.toList());

        model.addAttribute(nameOfAttributeForErrors, errors);
        redirectAttributes.addFlashAttribute(nameOfAttributeForErrors, errors);

        var referer = request.getHeader("Referer");

        return ((referer != null) ? "redirect:" + referer : "error/400");
    }

    /**
     * Обработчик ошибок, возникающих в процессе работы приложения
     *
     * @param exception          - ошибка, произошедшая в приложениия
     * @param redirectAttributes - класс для установки ошибок в атрибуты и послудующего их сохранения при редиректе
     */
    @ExceptionHandler(ApplicationRuntimeException.class)
    public String appRuntimeException(ApplicationRuntimeException exception,
                                      RedirectAttributes redirectAttributes,
                                      Model model,
                                      Locale locale) {

        var error = messageSource.getMessage(exception.getMessage(), exception.getArgs(), locale);

        model.addAttribute("error", error);
        redirectAttributes.addFlashAttribute("error", error);

        return "error/error-app";
    }

    /**
     * Обработчик ошибок, которые возникают при попытке выполнить
     * запрос для которого у пользователя не хватает прав
     */
    @ExceptionHandler(ApplicationForbiddenException.class)
    public String handleForbiddenException() {
        return "error/403";
    }
}
