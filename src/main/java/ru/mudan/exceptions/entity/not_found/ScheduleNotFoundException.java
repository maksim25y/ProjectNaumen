package ru.mudan.exceptions.entity.not_found;

import lombok.EqualsAndHashCode;
import ru.mudan.exceptions.base.EntityNotFoundException;

/**
 * Класс для исключений,
 * выбрасываемых когда сущность Schedule не найдена
 */
@EqualsAndHashCode(callSuper = true)
public class ScheduleNotFoundException extends EntityNotFoundException {
    public ScheduleNotFoundException(Long id) {
        super("schedule.not.found", id);
    }
}
