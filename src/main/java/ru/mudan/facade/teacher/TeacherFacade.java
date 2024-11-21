package ru.mudan.facade.teacher;

import org.springframework.stereotype.Component;
import ru.mudan.domain.entity.users.Teacher;
import ru.mudan.dto.teacher.TeacherDTO;
import ru.mudan.facade.BaseFacade;

@Component
public class TeacherFacade implements BaseFacade<Teacher, TeacherDTO> {

    @Override
    public TeacherDTO convertEntityToDTO(Teacher entity) {
        return TeacherDTO
                .builder()
                .id(entity.getId())
                .firstname(entity.getFirstname())
                .lastname(entity.getLastname())
                .patronymic(entity.getPatronymic())
                .email(entity.getEmail())
                .build();
    }
}
