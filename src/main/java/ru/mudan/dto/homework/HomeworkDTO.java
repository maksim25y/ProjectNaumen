package ru.mudan.dto.homework;

import java.time.LocalDate;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
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
        @NotNull(message = "{homework.subject.id.is_null}")
        Long classId,
        @NotNull(message = "{homework.subject.id.is_null}")
        Long subjectId
) {
}
