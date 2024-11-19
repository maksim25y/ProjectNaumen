package ru.mudan.dto.classes;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import java.util.List;
import lombok.Builder;
import org.hibernate.validator.constraints.Range;

/**
 * DTO для сущности ClassEntity
 */
@Builder
public record ClassDTO(
        Long id,
        @Pattern(regexp = "[А-Д]", message = "{letter.invalid_pattern}")
        @NotBlank(message = "{letter.is_blank}")
        String letter,
        @Range(min = 1, max = 11, message = "{number.range}")
        Integer number,
        String description,
        List<Long> studentsIds,
        List<Long> subjectsIds
) {
}
