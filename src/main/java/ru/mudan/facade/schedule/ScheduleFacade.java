package ru.mudan.facade.schedule;

import org.springframework.stereotype.Component;
import ru.mudan.domain.entity.Schedule;
import ru.mudan.dto.schedule.ScheduleDTO;
import ru.mudan.facade.BaseFacade;
import ru.mudan.util.ScheduleUtil;

/**
 * Класс для конвертации Schedule в ScheduleDTO
 */
@Component
public class ScheduleFacade implements BaseFacade<Schedule, ScheduleDTO> {

    /**
     * Метод для конвертации Schedule в ScheduleDTO
     *
     * @param entity - сущность Schedule для конвертации
     */
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
