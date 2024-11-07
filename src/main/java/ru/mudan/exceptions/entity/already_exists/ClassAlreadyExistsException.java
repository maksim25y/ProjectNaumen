package ru.mudan.exceptions.entity.already_exists;

import lombok.EqualsAndHashCode;
import ru.mudan.exceptions.base.ApplicationConflictException;

@EqualsAndHashCode(callSuper = true)
public class ClassAlreadyExistsException extends ApplicationConflictException {
    public ClassAlreadyExistsException(Integer number, String letter) {
        super("class.already.exists", new Object[]{number, letter});
    }
}
