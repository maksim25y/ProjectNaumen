package ru.mudan.dto.subjects;

import lombok.Builder;

/**
 * DTO для сущности Subject
 */
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
