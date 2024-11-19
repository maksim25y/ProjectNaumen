package ru.mudan.exceptions.entity.already_exists;

import lombok.EqualsAndHashCode;
import ru.mudan.exceptions.base.ApplicationConflictException;

/**
 * Класс для исключений,
 * выбрасываемых когда Subject
 * со значением email уже существует
 */
@EqualsAndHashCode(callSuper = true)
public class UserAlreadyExistsException extends ApplicationConflictException {

    public UserAlreadyExistsException(String email) {
        super("user.already.exists", new Object[]{email});
    }
}
