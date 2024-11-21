package ru.mudan.facade.classes;

import org.springframework.stereotype.Component;
import ru.mudan.domain.entity.ClassEntity;
import ru.mudan.dto.classes.ClassDTO;
import ru.mudan.facade.BaseFacade;

/**
 * Класс для конвертации ClassEntity в ClassDTO
 */
@Component
public class ClassFacade implements BaseFacade<ClassEntity, ClassDTO> {

    /**
     * Метод для конвертации ClassEntity в ClassDTO
     *
     * @param entity - сущность ClassEntity для конвертации
     */
    @Override
    public ClassDTO convertEntityToDTO(ClassEntity entity) {
        return ClassDTO
                .builder()
                .id(entity.getId())
                .number(entity.getNumber())
                .letter(entity.getLetter())
                .description(entity.getDescription())
                .build();
    }
}
