package ru.mudan.exceptions.entity.not_found;

import lombok.EqualsAndHashCode;
import ru.mudan.exceptions.base.ApplicationNotFoundException;

@EqualsAndHashCode(callSuper = true)
public class UserNotFoundException extends ApplicationNotFoundException {
    public UserNotFoundException(String email) {
        super("user.email.not.found", new Object[]{email});
    }
}