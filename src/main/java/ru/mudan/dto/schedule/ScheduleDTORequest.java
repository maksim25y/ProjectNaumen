package ru.mudan.dto.schedule;

import java.time.LocalTime;
import lombok.Builder;

@Builder
public record ScheduleDTORequest(
        Long id,
        Integer dayOfWeek,
        LocalTime startTime,
        Integer numberOfClassroom,
        Long classId,
        Long subjectId
) {
}
