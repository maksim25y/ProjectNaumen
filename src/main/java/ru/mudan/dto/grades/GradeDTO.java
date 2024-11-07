package ru.mudan.dto.grades;

import java.time.LocalDate;

import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import org.hibernate.validator.constraints.Range;

@Builder
public record GradeDTO(
        Long id,
        @Range(min = 2, max = 5, message = "{grade.mark.range}")
        Integer mark,
        @NotNull(message = "{date.of.mark.is_null}")
        LocalDate dateOfMark,
        String comment,
        Long studentId,
        Long subjectId
) {
}
