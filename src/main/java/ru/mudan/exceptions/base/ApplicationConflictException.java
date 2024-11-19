package ru.mudan.exceptions.base;

import lombok.EqualsAndHashCode;

/**
 * Абстрактный класс для исключений, которые
 * означают, что произошёл конфликт с данными в БД
 */
@EqualsAndHashCode(callSuper = true)
public abstract class ApplicationConflictException extends ApplicationRuntimeException {

    public ApplicationConflictException(String message, Object[] args) {
        super(message, args);
    }
}
