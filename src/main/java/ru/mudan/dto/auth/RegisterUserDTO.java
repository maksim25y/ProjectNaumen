package ru.mudan.dto.auth;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import java.util.List;
import lombok.Builder;

/**
 * Входные данные для регистрации нового пользователя
 *
 * @param firstname   - имя пользователя
 * @param lastname    - фамилия пользователя
 * @param patronymic  - отчество пользователя
 * @param email       - адрес электронной почты пользователя
 * @param password    - пароль пользователя
 * @param studentsIds - список id учеников (нужен для регистрации родителя)
 */
@Builder
public record RegisterUserDTO(
        @Size(min = 2, max = 15, message = "{firstname.invalid_size}")
        @Pattern(regexp = "[А-ЯЁ][а-яё]+", message = "{firstname.invalid_pattern}")
        @NotBlank(message = "{firstname.is_blank}")
        String firstname,
        @Size(min = 2, max = 15, message = "{lastname.invalid_size}")
        @Pattern(regexp = "[А-ЯЁ][а-яё]+", message = "{lastname.invalid_pattern}")
        @NotBlank(message = "{lastname.is_blank}")
        String lastname,
        @Size(min = 3, max = 15, message = "{patronymic.invalid_size}")
        @Pattern(regexp = "[А-ЯЁ][а-яё]+", message = "{patronymic.invalid_pattern}")
        @NotBlank(message = "{patronymic.is_blank}")
        String patronymic,
        @Pattern(message = "{email.invalid}",
                regexp = "^[\\w-.]+@([\\w-]+\\.)+[\\w-]{2,4}$")
        @NotBlank(message = "{email.is_blank}")
        String email,
        @Size(min = 8, max = 20, message = "{password.invalid_size}")
        @NotBlank(message = "{password.is_blank}")
        String password,
        List<Long> studentsIds
) {
}
