package ru.mudan.exceptions.base;

import lombok.EqualsAndHashCode;

/**
 * Класс для исключений, которые
 * означают, что данные не найдены
 */
@EqualsAndHashCode(callSuper = true)
public abstract class ApplicationNotFoundException extends ApplicationRuntimeException {

    public ApplicationNotFoundException(String message, Object[] args) {
        super(message, args);
    }
}
