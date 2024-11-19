package ru.mudan.exceptions.entity.not_found;

import lombok.EqualsAndHashCode;
import ru.mudan.exceptions.base.EntityNotFoundException;

/**
 * Класс для исключений,
 * выбрасываемых когда сущность Student не найдена
 */
@EqualsAndHashCode(callSuper = true)
public class StudentNotFoundException extends EntityNotFoundException {
    public StudentNotFoundException(Long id) {
        super("student.not.found", id);
    }
}
