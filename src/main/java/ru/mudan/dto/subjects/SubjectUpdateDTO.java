package ru.mudan.dto.subjects;

import jakarta.validation.constraints.NotBlank;
import lombok.Builder;

/**
 * Входные данные для обновления предмета
 */
@Builder
public record SubjectUpdateDTO(
        @NotBlank(message = "{subject.type.is_blank}")
        String type,
        String description
) {
}