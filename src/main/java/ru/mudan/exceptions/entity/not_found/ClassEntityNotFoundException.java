package ru.mudan.exceptions.entity.not_found;


import ru.mudan.exceptions.base.EntityNotFoundException;

public class ClassEntityNotFoundException extends EntityNotFoundException {
    public ClassEntityNotFoundException(Long id) {
        super("class.not.found", id);
    }
}
