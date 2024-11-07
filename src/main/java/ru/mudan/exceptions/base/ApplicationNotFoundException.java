package ru.mudan.exceptions.base;

import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
public abstract class ApplicationNotFoundException extends ApplicationRuntimeException {

    public ApplicationNotFoundException(String message, Object[] args) {
        super(message, args);
    }
}
