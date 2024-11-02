package ru.mudan.dto;

import lombok.Builder;

@Builder
public record RegisterUserDTO(
        String firstname,
        String lastname,
        String patronymic,
        String email,
        String password
) {
}
