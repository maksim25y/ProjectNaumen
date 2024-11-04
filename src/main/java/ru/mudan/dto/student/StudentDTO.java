package ru.mudan.dto.student;

import lombok.Builder;

@Builder
public record StudentDTO(
        Long id,
        String firstname,
        String lastname,
        String patronymic,
        String email
) {
}
