package ru.mudan.dto.subjects;

import jakarta.validation.constraints.NotBlank;
import lombok.Builder;

@Builder
public record SubjectUpdateDTO(
        String code,
        @NotBlank(message = "{subject.type.is_blank}")
        String type,
        String description
) {
}