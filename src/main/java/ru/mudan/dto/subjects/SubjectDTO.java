package ru.mudan.dto.subjects;

import lombok.Builder;

@Builder
public record SubjectDTO(
        Long id,
        String name,
        String code,
        String type,
        String description,
        Long classId
) {
}
