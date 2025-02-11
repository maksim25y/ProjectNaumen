package ru.mudan.facade.subjects;

import org.springframework.stereotype.Component;
import ru.mudan.domain.entity.Subject;
import ru.mudan.dto.subjects.SubjectDTO;
import ru.mudan.facade.BaseFacade;

/**
 * Класс для конвертации Subject в SubjectDTO
 */
@Component
public class SubjectFacade implements BaseFacade<Subject, SubjectDTO> {

    /**
     * Метод для конвертации Subject в SubjectDTO
     *
     * @param entity - сущность Subject для конвертации
     */
    @Override
    public SubjectDTO convertEntityToDTO(Subject entity) {
        return SubjectDTO
                .builder()
                .id(entity.getId())
                .code(entity.getCode())
                .name(entity.getName())
                .description(entity.getDescription())
                .type(entity.getType())
                .build();
    }
}
