package ru.mudan.facade.homework;

import org.springframework.stereotype.Component;
import ru.mudan.domain.entity.Homework;
import ru.mudan.dto.homework.HomeworkDTO;
import ru.mudan.facade.BaseFacade;

@Component
public class HomeworkFacade implements BaseFacade<Homework, HomeworkDTO> {

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
