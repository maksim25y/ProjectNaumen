package ru.mudan.exceptions.entity.not_found;


import lombok.EqualsAndHashCode;
import ru.mudan.exceptions.base.EntityNotFoundException;

@EqualsAndHashCode(callSuper = true)
public class ClassEntityNotFoundException extends EntityNotFoundException {

    public ClassEntityNotFoundException(Long id) {
        super("class.not.found", id);
    }
}