package ru.mudan.services.students;

import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
import ru.mudan.facade.student.StudentFacade;
import ru.mudan.services.auth.MyUserDetailsService;

/**
 * Класс с описанием бизнес-логики
 * для работы с сущностью Student
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class StudentService {

    private final StudentRepository studentRepository;
    private final ClassRepository classRepository;
    private final SubjectsRepository subjectsRepository;
    private final MyUserDetailsService myUserDetailsService;
    private final ParentRepository parentRepository;
    private final StudentFacade studentFacade;

    /**
     * Метод для получения ученика по id
     *
     * @param id - id ученика
     */
    public StudentDTO findById(Long id) {
        var foundStudent = getStudentById(id);

        return studentFacade.convertEntityToDTO(foundStudent);
    }

    /**
     * Метод для получения учеников без класса
     */
    public List<StudentDTO> findStudentsWithNotClass() {
        log.info("Getting all students with not class");
        return studentRepository.findAllByClassEntity(null)
                .stream()
                .map(studentFacade::convertEntityToDTO)
                .toList();
    }

    /**
     * Метод для получения учеников для класса
     *
     * @param id - id класса
     */
    public List<StudentDTO> findAllStudentsForClass(Long id) {
        log.info("Started getting all students for class with id={}", id);
        var foundClass = classRepository.findById(id)
                .orElseThrow(() -> new ClassEntityNotFoundException(id));
        log.info("Finished getting all students for class with id={}", id);

        return foundClass.getStudents()
                .stream()
                .map(studentFacade::convertEntityToDTO)
                .toList();
    }

    /**
     * Метод для получения учеников без назначенного родителя
     */
    public List<StudentDTO> findAllStudentsWithNotParent() {
        var students = studentRepository.findAllByParent(null);

        return students.stream()
                .map(studentFacade::convertEntityToDTO)
                .toList();
    }

    /**
     * Метод для получения учеников для родителя
     *
     * @param parentId - id родителя
     */
    public List<StudentDTO> getAllStudentsForParent(Long parentId) {
        log.info("Started getting all students for parent with id={}", parentId);
        var parent = parentRepository.findById(parentId)
                .orElseThrow(() -> new ParentNotFoundException(parentId));
        log.info("Finished getting all students for parent with id={}", parentId);

        return parent.getStudents().stream()
                .map(studentFacade::convertEntityToDTO)
                .toList();
    }

    /**
     * Метод для получения учеников по предмету
     *
     * @param subjectId - id предмета
     */
    public List<StudentDTO> findAllStudentsBySubjectId(Long subjectId) {
        var foundSubject = subjectsRepository.findById(subjectId)
                .orElseThrow(() -> new SubjectNotFoundException(subjectId));

        var classEntity = foundSubject.getClassEntity();

        return classEntity.getStudents().stream()
                .map(studentFacade::convertEntityToDTO)
                .toList();
    }

    /**
     * Метод для получения ученика по аутентификации
     *
     * @param authentication - текущая аутентификация
     */
    public StudentDTO findStudentByAuth(Authentication authentication) {
        var student = (Student) myUserDetailsService.loadUserByUsername(authentication.getName());

        return studentFacade.convertEntityToDTO(student);
    }

    public List<StudentDTO> findAll() {
        var allStudents = studentRepository.findAll();

        return allStudents.stream()
                .map(studentFacade::convertEntityToDTO)
                .toList();
    }

    /**
     * Метод для получения ученика по id
     *
     * @param id - id ученика
     */
    private Student getStudentById(Long id) {
        log.info("Getting student with id {}", id);
        return studentRepository.findById(id)
                .orElseThrow(() -> new StudentNotFoundException(id));
    }
}
