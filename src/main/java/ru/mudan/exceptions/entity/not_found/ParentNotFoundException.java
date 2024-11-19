package ru.mudan.exceptions.entity.not_found;

import lombok.EqualsAndHashCode;
import ru.mudan.exceptions.base.EntityNotFoundException;

/**
 * Класс для исключений,
 * выбрасываемых когда сущность Parent не найдена
 */
@EqualsAndHashCode(callSuper = true)
public class ParentNotFoundException extends EntityNotFoundException {
    public ParentNotFoundException(Long id) {
        super("parent.not.found", id);
    }
}
