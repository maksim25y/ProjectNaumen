package ru.mudan.facade.grades;

import org.springframework.stereotype.Component;
import ru.mudan.domain.entity.Grade;
import ru.mudan.dto.grades.GradeDTO;
import ru.mudan.facade.BaseFacade;

@Component
public class GradeFacade implements BaseFacade<Grade, GradeDTO> {

    @Override
    public GradeDTO convertEntityToDTO(Grade entity) {
        return GradeDTO
                .builder()
                .id(entity.getId())
                .mark(entity.getMark())
                .dateOfMark(entity.getDateOfMark())
                .comment(entity.getComment())
                .studentId(entity.getStudent().getId())
                .subjectId(entity.getSubject().getId())
                .build();
    }
}
