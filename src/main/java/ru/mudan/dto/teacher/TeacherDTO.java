package ru.mudan.dto.teacher;

import lombok.Builder;

@Builder
public record TeacherDTO(
        Long id,
        String firstname,
        String lastname,
        String patronymic,
        String email
) {
}
