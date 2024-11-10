package ru.mudan.dto.parent;

import lombok.Builder;

@Builder
public record ParentDTO(
        Long id,
        String firstname,
        String lastname,
        String patronymic,
        String email
) {
}