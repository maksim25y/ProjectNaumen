package ru.mudan.exceptions;

import lombok.EqualsAndHashCode;
import ru.mudan.exceptions.base.ApplicationRuntimeException;

/**
 * Базовый класс для исключений,
 * выбрасываемых приложением из-за ошибки на стороне сервера
 */
@EqualsAndHashCode(callSuper = true)
public abstract class ApplicationInternalServerErrorException extends ApplicationRuntimeException {

    public ApplicationInternalServerErrorException(String message, Object[] args) {
        super(message, args);
    }
}