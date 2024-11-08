package ru.mudan.exceptions.entity.not_found;

import lombok.EqualsAndHashCode;
import ru.mudan.exceptions.base.EntityNotFoundException;

@EqualsAndHashCode(callSuper = true)
public class GradeNotFoundException extends EntityNotFoundException {
    public GradeNotFoundException(Long id) {
        super("grade.not.found", id);
    }
}
