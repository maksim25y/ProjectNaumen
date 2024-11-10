package ru.mudan.services.students;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import ru.mudan.domain.entity.users.Student;
import ru.mudan.domain.repositories.ClassRepository;
import ru.mudan.domain.repositories.ParentRepository;
import ru.mudan.domain.repositories.StudentRepository;
import ru.mudan.domain.repositories.SubjectsRepository;
import ru.mudan.dto.student.StudentDTO;
import ru.mudan.exceptions.entity.not_found.ClassEntityNotFoundException;
import ru.mudan.exceptions.entity.not_found.ParentNotFoundException;
import ru.mudan.exceptions.entity.not_found.StudentNotFoundException;
import ru.mudan.exceptions.entity.not_found.SubjectNotFoundException;
import ru.mudan.services.auth.MyUserDetailsService;

@Service
@RequiredArgsConstructor
public class StudentService {

    private final StudentRepository studentRepository;
    private final ClassRepository classRepository;
    private final SubjectsRepository subjectsRepository;
    private final MyUserDetailsService myUserDetailsService;
    private final ParentRepository parentRepository;

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

    public List<StudentDTO> findAllStudentsWithNotParent() {
        var students = studentRepository.findAllByParent(null);

        return students.stream()
                .map(st -> StudentDTO
                        .builder()
                        .id(st.getId())
                        .firstname(st.getFirstname())
                        .lastname(st.getLastname())
                        .patronymic(st.getPatronymic())
                        .email(st.getEmail())
                        .classId(st.getClassEntity() != null ? st.getClassEntity().getId() : null)
                        .build())
                .toList();
    }

    public List<StudentDTO> getAllStudentsForParent(Long parentId) {
        var parent = parentRepository.findById(parentId)
                .orElseThrow(() -> new ParentNotFoundException(parentId));

        return parent.getStudents().stream()
                .map(st -> StudentDTO
                        .builder()
                        .id(st.getId())
                        .firstname(st.getFirstname())
                        .lastname(st.getLastname())
                        .patronymic(st.getPatronymic())
                        .email(st.getEmail())
                        .classId(st.getClassEntity() != null ? st.getClassEntity().getId() : null)
                        .build())
                .toList();
    }

    public List<StudentDTO> findAllStudentsBySubjectId(Long subjectId) {
        var foundSubject = subjectsRepository.findById(subjectId)
                .orElseThrow(() -> new SubjectNotFoundException(subjectId));

        var classEntity = foundSubject.getClassEntity();

        return classEntity.getStudents().stream()
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

    public StudentDTO findStudentByAuth(Authentication authentication) {
        var student = (Student) myUserDetailsService.loadUserByUsername(authentication.getName());

        Long classId = null;
        var classEntity = student.getClassEntity();

        if (classEntity != null) {
            classId = classEntity.getId();
        }

        return StudentDTO
                .builder()
                .id(student.getId())
                .firstname(student.getFirstname())
                .lastname(student.getLastname())
                .patronymic(student.getPatronymic())
                .email(student.getEmail())
                .classId(classId)
                .build();
    }
}
