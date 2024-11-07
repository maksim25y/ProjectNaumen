package ru.mudan.exceptions.base;

import lombok.EqualsAndHashCode;
import lombok.Getter;

@Getter
@EqualsAndHashCode(callSuper = true)
public abstract class ApplicationRuntimeException extends RuntimeException {

    private final Object[] args;

    public ApplicationRuntimeException(String message, Object[] args) {
        super(message);
        this.args = args;
    }
}
