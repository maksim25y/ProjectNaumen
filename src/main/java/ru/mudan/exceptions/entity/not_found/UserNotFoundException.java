package ru.mudan.exceptions.entity.not_found;

import lombok.EqualsAndHashCode;
import ru.mudan.exceptions.base.ApplicationNotFoundException;

/**
 * Класс для исключений,
 * выбрасываемых когда сущность ApplicationUser не найдена
 */
@EqualsAndHashCode(callSuper = true)
public class UserNotFoundException extends ApplicationNotFoundException {
    public UserNotFoundException(String email) {
        super("user.email.not.found", new Object[]{email});
    }
}