package ru.mudan.facade.homework;

import org.springframework.stereotype.Component;
import ru.mudan.domain.entity.Homework;
import ru.mudan.dto.homework.HomeworkDTO;
import ru.mudan.facade.BaseFacade;

/**
 * Класс для конвертации Homework в HomeworkDTO
 */
@Component
public class HomeworkFacade implements BaseFacade<Homework, HomeworkDTO> {

    /**
     * Метод для конвертации Homework в HomeworkDTO
     *
     * @param entity - сущность Homework для конвертации
     */
    @Override
    public HomeworkDTO convertEntityToDTO(Homework entity) {
        return HomeworkDTO
                .builder()
                .id(entity.getId())
                .title(entity.getTitle())
                .description(entity.getDescription())
                .deadline(entity.getDeadline())
                .classId(entity.getClassEntity().getId())
                .subjectId(entity.getSubject().getId())
                .build();
    }
}
