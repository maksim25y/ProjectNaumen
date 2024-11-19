package ru.mudan.exceptions.base;

import lombok.EqualsAndHashCode;
import lombok.Getter;

/**
 * Абстрактный класс для исключений, которые
 * могут произойти во время работы приложения
 */
@Getter
@EqualsAndHashCode(callSuper = true)
public abstract class ApplicationRuntimeException extends RuntimeException {

    private final Object[] args;

    public ApplicationRuntimeException(String message, Object[] args) {
        super(message);
        this.args = args;
    }
}
