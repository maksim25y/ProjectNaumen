package ru.mudan.exceptions.entity.already_exists;

import lombok.EqualsAndHashCode;
import ru.mudan.exceptions.base.ApplicationConflictException;

/**
 * Класс для исключений,
 * выбрасываемых когда ClassEntity
 * со значением letter и number уже существует
 */
@EqualsAndHashCode(callSuper = true)
public class ClassAlreadyExistsException extends ApplicationConflictException {
    public ClassAlreadyExistsException(Integer number, String letter) {
        super("class.already.exists", new Object[]{number, letter});
    }
}
