package ru.mudan.dto.schedule;

import jakarta.validation.constraints.NotNull;
import java.time.LocalTime;
import lombok.Builder;
import org.hibernate.validator.constraints.Range;

/**
 * Входные данные для обновления ячейки расписания
 */
@Builder
public record ScheduleUpdateDTO(
        @Range(min = 1, max = 5, message = "{schedule.day.of.week.range}")
        Integer dayOfWeek,
        @NotNull(message = "{schedule.start.time.is_null}")
        LocalTime startTime,
        @Range(min = 1, max = 300, message = "{schedule.number.if.classroom.range}")
        Integer numberOfClassroom
) {
}
