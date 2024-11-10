package ru.mudan.exceptions.entity.not_found;

import lombok.EqualsAndHashCode;
import ru.mudan.exceptions.base.EntityNotFoundException;

@EqualsAndHashCode(callSuper = true)
public class ParentNotFoundException extends EntityNotFoundException {
    public ParentNotFoundException(Long id) {
        super("parent.not.found", id);
    }
}
