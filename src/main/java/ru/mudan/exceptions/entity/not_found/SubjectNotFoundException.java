package ru.mudan.exceptions.entity.not_found;

import lombok.EqualsAndHashCode;
import ru.mudan.exceptions.base.EntityNotFoundException;

/**
 * Класс для исключений,
 * выбрасываемых когда сущность Subject не найдена
 */
@EqualsAndHashCode(callSuper = true)
public class SubjectNotFoundException extends EntityNotFoundException {

    public SubjectNotFoundException(Long id) {
        super("subject.not.found", id);
    }
}
