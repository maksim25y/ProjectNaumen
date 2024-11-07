package ru.mudan.dto.homework;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;
import lombok.Builder;

@Builder
public record HomeworkDTO(
        Long id,
        @NotBlank(message = "{homework.title.is_blank}")
        String title,
        @NotBlank(message = "{homework.description.is_blank}")
        String description,
        @NotNull(message = "{homework.deadline.is_null}")
        LocalDate deadline,
        Long classId,
        Long subjectId
) {
}
