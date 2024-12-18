package ru.mudan.dto.subjects;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Builder;

/**
 * Входные данные для создания предмета
 *
 * @param name        - название предмета
 * @param type        - тип предмета
 * @param description - описание предмета
 * @param classId     - id класса, для которого создаётся предмет
 * @param teacherId   - id учителя, который будет вести предмет
 */
@Builder
public record SubjectCreateDTO(
        @Pattern(regexp = "[А-ЯЁ][а-яё\\s]*", message = "{subject.name.invalid_pattern}")
        @NotBlank(message = "{subject.name.is_blank}")
        @Size(min = 4, max = 20, message = "{subject.name.invalid_size}")
        String name,
        @NotBlank(message = "{subject.type.is_blank}")
        String type,
        String description,
        @NotNull(message = "{subject.class.id.is_blank}")
        Long classId,
        @NotNull(message = "{subject.teacher.id.is_blank}")
        Long teacherId
) {
}