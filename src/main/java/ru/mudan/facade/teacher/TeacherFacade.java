package ru.mudan.facade.teacher;

import org.springframework.stereotype.Component;
import ru.mudan.domain.entity.users.Teacher;
import ru.mudan.dto.teacher.TeacherDTO;
import ru.mudan.facade.BaseFacade;

/**
 * Класс для конвертации Teacher в TeacherDTO
 */
@Component
public class TeacherFacade implements BaseFacade<Teacher, TeacherDTO> {

    /**
     * Метод для конвертации Teacher в TeacherDTO
     *
     * @param entity - сущность Teacher для конвертации
     */
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
