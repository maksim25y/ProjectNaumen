package ru.mudan.services.students;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.mudan.domain.repositories.StudentRepository;
import ru.mudan.dto.student.StudentDTO;
import ru.mudan.exceptions.entity.not_found.StudentNotFoundException;

@Service
@RequiredArgsConstructor
public class StudentService {

    private final StudentRepository studentRepository;

    public StudentDTO findById(Long id) {
        var foundStudent =  studentRepository.findById(id)
                .orElseThrow(() -> new StudentNotFoundException(id));

        return StudentDTO
                .builder()
                .id(foundStudent.getId())
                .firstname(foundStudent.getFirstname())
                .lastname(foundStudent.getLastname())
                .patronymic(foundStudent.getPatronymic())
                .email(foundStudent.getEmail())
                .build();
    }
}
