package ru.mudan.dto.grades;

import java.time.LocalDate;
import lombok.Builder;

@Builder
public record GradeDTO(
        Long id,
        Integer mark,
        LocalDate dateOfMark,
        String comment,
        Long studentId,
        Long subjectId
) {
}
