package ru.mudan.services.students;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.mudan.domain.repositories.ClassRepository;
import ru.mudan.domain.repositories.StudentRepository;
import ru.mudan.dto.student.StudentDTO;
import ru.mudan.exceptions.entity.not_found.ClassEntityNotFoundException;
import ru.mudan.exceptions.entity.not_found.StudentNotFoundException;

@Service
@RequiredArgsConstructor
public class StudentService {

    private final StudentRepository studentRepository;
    private final ClassRepository classRepository;

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

    public List<StudentDTO> findStudentsWithNotClass() {
        return studentRepository.findAllByClassEntity(null)
                .stream()
                .map(st -> StudentDTO
                        .builder()
                        .id(st.getId())
                        .firstname(st.getFirstname())
                        .lastname(st.getLastname())
                        .patronymic(st.getPatronymic())
                        .email(st.getEmail())
                        .build())
                .toList();
    }

    public List<StudentDTO> findAllStudentsForClass(Long id) {
        var foundClass = classRepository.findById(id)
                .orElseThrow(() -> new ClassEntityNotFoundException(id));

        return foundClass.getStudents()
                .stream()
                .map(st -> StudentDTO
                        .builder()
                        .id(st.getId())
                        .firstname(st.getFirstname())
                        .lastname(st.getLastname())
                        .patronymic(st.getPatronymic())
                        .email(st.getEmail())
                        .build())
                .toList();
    }
}
