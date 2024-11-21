package ru.mudan.facade.schedule;

import org.springframework.stereotype.Component;
import ru.mudan.domain.entity.Schedule;
import ru.mudan.dto.schedule.ScheduleDTO;
import ru.mudan.facade.BaseFacade;
import ru.mudan.util.ScheduleUtil;

@Component
public class ScheduleFacade implements BaseFacade<Schedule, ScheduleDTO> {

    @Override
    public ScheduleDTO convertEntityToDTO(Schedule entity) {
        return ScheduleDTO
                .builder()
                .id(entity.getId())
                .numberOfClassRoom(entity.getNumberOfClassroom())
                .dayOfWeek(ScheduleUtil.days.get(entity.getDayOfWeek()))
                .subjectName(entity.getSubject().getName())
                .startTime(entity.getStartTime())
                .build();
    }
}
