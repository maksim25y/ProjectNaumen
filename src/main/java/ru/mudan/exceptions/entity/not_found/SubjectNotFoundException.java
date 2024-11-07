package ru.mudan.exceptions.entity.not_found;

import lombok.EqualsAndHashCode;
import ru.mudan.exceptions.base.EntityNotFoundException;

@EqualsAndHashCode(callSuper = true)
public class SubjectNotFoundException extends EntityNotFoundException {

    public SubjectNotFoundException(Long id) {
        super("subject.not.found", id);
    }
}
