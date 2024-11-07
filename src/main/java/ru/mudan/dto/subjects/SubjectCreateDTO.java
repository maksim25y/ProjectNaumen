package ru.mudan.dto.subjects;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Builder;

@Builder
public record SubjectCreateDTO(
        @Pattern(regexp = "[А-ЯЁ][а-яё\\s]*", message = "{subject.name.invalid_pattern}")
        @NotBlank(message = "{subject.name.is_blank}")
        String name,
        @NotBlank(message = "{subject.type.is_blank}")
        String type,
        String description,
        Long classId
) {
}