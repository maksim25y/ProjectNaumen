package ru.mudan.exceptions.base;

import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
public abstract class ApplicationConflictException extends ApplicationRuntimeException {

    public ApplicationConflictException(String message, Object[] args) {
        super(message, args);
    }
}
