package ru.mudan.dto;

import java.time.LocalDate;
import lombok.Builder;

@Builder
public record HomeworkDTO(
        Long id,
        String title,
        String description,
        LocalDate deadline,
        Long classId,
        Long subjectId
) {
}
