package ru.mudan.dto.grades;

import java.time.LocalDate;
import lombok.Builder;

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
