package ru.mudan.exceptions;

import lombok.EqualsAndHashCode;

/**
 * Класс для исключений,
 * выбрасываемых из-за ошибки при отправке письма на почту
 */
@EqualsAndHashCode(callSuper = true)
public final class NotificationException extends ApplicationInternalServerErrorException {

    public NotificationException() {
        super("email.creation.error", new Object[]{});
    }
}
