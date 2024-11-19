package ru.mudan.exceptions.entity.not_found;

import lombok.EqualsAndHashCode;
import ru.mudan.exceptions.base.EntityNotFoundException;

/**
 * Класс для исключений,
 * выбрасываемых когда сущность Homework не найдена
 */
@EqualsAndHashCode(callSuper = true)
public class HomeworkNotFoundException extends EntityNotFoundException {
    public HomeworkNotFoundException(Long id) {
        super("homework.not.found", id);
    }
}
