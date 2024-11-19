package ru.mudan.dto.parent;

import lombok.Builder;

/**
 * DTO для сущности Parent
 */
@Builder
public record ParentDTO(
        Long id,
        String firstname,
        String lastname,
        String patronymic,
        String email
) {
}