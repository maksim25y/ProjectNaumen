package ru.mudan.exceptions.entity.not_found;

import lombok.EqualsAndHashCode;
import ru.mudan.exceptions.base.EntityNotFoundException;

@EqualsAndHashCode(callSuper = true)
public class TeacherNotFoundException extends EntityNotFoundException {
    public TeacherNotFoundException(Long id) {
        super("teacher.not.found", id);
    }
}
