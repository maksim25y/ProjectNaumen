package ru.mudan.facade.student;

import org.springframework.stereotype.Component;
import ru.mudan.domain.entity.users.Student;
import ru.mudan.dto.student.StudentDTO;
import ru.mudan.facade.BaseFacade;

/**
 * Класс для конвертации Student в StudentDTO
 */
@Component
public class StudentFacade implements BaseFacade<Student, StudentDTO> {

    /**
     * Метод для конвертации Student в StudentDTO
     *
     * @param entity - сущность Student для конвертации
     */
    @Override
    public StudentDTO convertEntityToDTO(Student entity) {
        return StudentDTO
                .builder()
                .id(entity.getId())
                .firstname(entity.getFirstname())
                .lastname(entity.getLastname())
                .patronymic(entity.getPatronymic())
                .email(entity.getEmail())
                .classId(entity.getClassEntity() != null ? entity.getClassEntity().getId() : null)
                .build();
    }
}
