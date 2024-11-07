package ru.mudan.controller;

import jakarta.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.context.MessageSource;
import org.springframework.ui.Model;
import org.springframework.validation.BindException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@ControllerAdvice
@RequiredArgsConstructor
public class ExceptionHandlerController {

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
        model.addAttribute("errors", errors);
        redirectAttributes.addFlashAttribute("errors", errors);


        String referer = request.getHeader("Referer");

        if (referer != null) {
            return "redirect:" + referer;
        } else {
            return "redirect:/test-400";
        }
    }
}
