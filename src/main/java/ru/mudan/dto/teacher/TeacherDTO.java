package ru.mudan.dto.teacher;

import lombok.Builder;

/**
 * DTO для сущности Teacher
 */
@Builder
public record TeacherDTO(
        Long id,
        String firstname,
        String lastname,
        String patronymic,
        String email
) {
}
