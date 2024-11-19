package ru.mudan.exceptions;

import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
public final class NotificationException extends ApplicationInternalServerErrorException {

    public NotificationException() {
        super("email.creation.error", new Object[]{});
    }
}
