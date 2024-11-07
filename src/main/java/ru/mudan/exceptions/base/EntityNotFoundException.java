package ru.mudan.exceptions.base;

import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
public abstract class EntityNotFoundException extends ApplicationNotFoundException {

    public EntityNotFoundException(String message, Long id) {
        super(message, new Object[]{id});
    }
}
