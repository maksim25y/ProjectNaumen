package ru.mudan.dto.student;

import lombok.Builder;

/**
 * DTO для сущности Student
 */
@Builder
public record StudentDTO(
        Long id,
        String firstname,
        String lastname,
        String patronymic,
        String email,
        Long classId
) {
}
