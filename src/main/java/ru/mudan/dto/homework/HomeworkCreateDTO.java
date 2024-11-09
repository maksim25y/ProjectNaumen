package ru.mudan.dto.homework;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;

public record HomeworkCreateDTO(
        Long id,
        @NotBlank(message = "{homework.title.is_blank}")
        String title,
        @NotBlank(message = "{homework.description.is_blank}")
        String description,
        @NotNull(message = "{homework.deadline.is_null}")
        LocalDate deadline,
        @NotNull(message = "{homework.subject.id.is_null}")
        Long subjectId
) {
}
