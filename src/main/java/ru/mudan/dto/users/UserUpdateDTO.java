package ru.mudan.dto.users;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Builder;

@Builder
public record UserUpdateDTO(
        @Size(min = 4, max = 15, message = "{firstname.invalid_size}")
        @Pattern(regexp = "[А-ЯЁ][а-яё]+", message = "{firstname.invalid_pattern}")
        @NotBlank(message = "{firstname.is_blank}")
        String firstname,
        @Size(min = 4, max = 15, message = "{lastname.invalid_size}")
        @Pattern(regexp = "[А-ЯЁ][а-яё]+", message = "{lastname.invalid_pattern}")
        @NotBlank(message = "{lastname.is_blank}")
        String lastname,
        @Size(min = 4, max = 15, message = "{patronymic.invalid_size}")
        @Pattern(regexp = "[А-ЯЁ][а-яё]+", message = "{patronymic.invalid_pattern}")
        @NotBlank(message = "{patronymic.is_blank}")
        String patronymic
){
}
