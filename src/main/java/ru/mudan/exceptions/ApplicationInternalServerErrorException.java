package ru.mudan.exceptions;

import lombok.EqualsAndHashCode;
import ru.mudan.exceptions.base.ApplicationRuntimeException;

@EqualsAndHashCode(callSuper = true)
public abstract class ApplicationInternalServerErrorException extends ApplicationRuntimeException {

    public ApplicationInternalServerErrorException(String message, Object[] args) {
        super(message, args);
    }
}