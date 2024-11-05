package ru.mudan.dto;

import java.time.LocalDate;
import lombok.Builder;

@Builder
public record HomeworkDTO(
        String title,
        String description,
        LocalDate deadline,
        Long classId,
        Long subjectId
) {
}
