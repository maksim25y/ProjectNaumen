package ru.mudan.exceptions.entity.not_found;

import lombok.EqualsAndHashCode;
import ru.mudan.exceptions.base.EntityNotFoundException;

@EqualsAndHashCode(callSuper = true)
public class ScheduleNotFoundException extends EntityNotFoundException {
    public ScheduleNotFoundException(Long id) {
        super("schedule.not.found", id);
    }
}
