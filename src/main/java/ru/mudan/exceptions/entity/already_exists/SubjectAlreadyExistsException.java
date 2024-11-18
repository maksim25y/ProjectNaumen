package ru.mudan.exceptions.entity.already_exists;

import lombok.EqualsAndHashCode;
import ru.mudan.exceptions.base.ApplicationConflictException;

/**
 * Класс для исключений,
 * выбрасываемых когда Subject
 * со значением name уже существует
 */
@EqualsAndHashCode(callSuper = true)
public class SubjectAlreadyExistsException extends ApplicationConflictException {
    public SubjectAlreadyExistsException(String name, Integer number, String letter) {
        super("subject.already.exists", new Object[]{name, number, letter});
    }
}

