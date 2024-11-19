package ru.mudan.dto.schedule;

import java.time.LocalTime;
import lombok.Builder;

/**
 * DTO для сущности Schedule
 */
@Builder
public record ScheduleDTO(
        Long id,
        String dayOfWeek,
        LocalTime startTime,
        Integer numberOfClassRoom,
        String subjectName
) {
}
