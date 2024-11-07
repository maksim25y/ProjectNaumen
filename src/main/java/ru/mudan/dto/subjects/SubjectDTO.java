package ru.mudan.dto.subjects;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Builder;

@Builder
public record SubjectDTO(
        Long id,
        @Pattern(regexp = "[А-ЯЁ][а-яё\\s]*", message = "{subject.name.invalid_pattern}")
        @NotBlank(message = "{subject.name.is_blank}")
        String name,
        @Pattern(regexp = "[A-Z0-9]+", message = "{subject.code.invalid_pattern}")
        @NotBlank(message = "{subject.code.is_blank}")
        String code,
        @NotBlank(message = "{subject.type.is_blank}")
        String type,
        String description
) {
}
