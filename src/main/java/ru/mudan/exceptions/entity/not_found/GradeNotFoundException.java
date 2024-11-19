package ru.mudan.exceptions.entity.not_found;

import lombok.EqualsAndHashCode;
import ru.mudan.exceptions.base.EntityNotFoundException;

/**
 * Класс для исключений,
 * выбрасываемых когда сущность Grade не найдена
 */
@EqualsAndHashCode(callSuper = true)
public class GradeNotFoundException extends EntityNotFoundException {
    public GradeNotFoundException(Long id) {
        super("grade.not.found", id);
    }
}
