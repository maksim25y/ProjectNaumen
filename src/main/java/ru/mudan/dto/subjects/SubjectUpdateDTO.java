package ru.mudan.dto.subjects;

import jakarta.validation.constraints.NotBlank;
import lombok.Builder;

/**
 * Входные данные для обновления предмета
 *
 * @param type        - тип предмета
 * @param description - описание предмета
 */
@Builder
public record SubjectUpdateDTO(
        @NotBlank(message = "{subject.type.is_blank}")
        String type,
        String description
) {
}