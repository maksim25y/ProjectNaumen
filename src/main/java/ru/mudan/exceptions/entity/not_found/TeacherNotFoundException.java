package ru.mudan.exceptions.entity.not_found;

import lombok.EqualsAndHashCode;
import ru.mudan.exceptions.base.EntityNotFoundException;

/**
 * Класс для исключений,
 * выбрасываемых когда сущность Teacher не найдена
 */
@EqualsAndHashCode(callSuper = true)
public class TeacherNotFoundException extends EntityNotFoundException {
    public TeacherNotFoundException(Long id) {
        super("teacher.not.found", id);
    }
}
