package ru.mudan.dto.grades;

import java.time.LocalDate;
import lombok.Builder;

/**
 * DTO ответа на запрос к сущности Grade
 */
@Builder
public record GradeDTOResponse(
        Long id,
        Integer mark,
        LocalDate dateOfMark,
        String comment,
        String studentFirstname,
        String studentLastname
){
}
