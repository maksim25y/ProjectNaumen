package ru.mudan.exceptions.entity.not_found;

import lombok.EqualsAndHashCode;
import ru.mudan.exceptions.base.EntityNotFoundException;

@EqualsAndHashCode(callSuper = true)
public class HomeworkNotFoundException extends EntityNotFoundException {
    public HomeworkNotFoundException(Long id) {
        super("homework.not.found", id);
    }
}
