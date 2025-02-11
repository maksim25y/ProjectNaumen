package ru.mudan.dto.schedule;

import jakarta.validation.constraints.NotNull;
import java.time.LocalTime;
import lombok.Builder;
import org.hibernate.validator.constraints.Range;

/**
 * Входные данные для создания ячейки расписания
 *
 * @param dayOfWeek         - день недели (от понедельника до пятницы)
 * @param startTime         - время начала занятия
 * @param numberOfClassroom - номер классной комнаты
 * @param classId           - id класса, для которого создаётся ячейка расписания
 * @param subjectId         - id предмета, для которого создаётся ячейка расписания
 */
@Builder
public record ScheduleCreateDTO(
        @Range(min = 1, max = 5, message = "{schedule.day.of.week.range}")
        Integer dayOfWeek,
        @NotNull(message = "{schedule.start.time.is_null}")
        LocalTime startTime,
        @Range(min = 1, max = 300, message = "{schedule.number.if.classroom.range}")
        Integer numberOfClassroom,
        @NotNull(message = "{schedule.class.id.is_null}")
        Long classId,
        @NotNull(message = "{schedule.subject.id.is_null}")
        Long subjectId
) {
}
