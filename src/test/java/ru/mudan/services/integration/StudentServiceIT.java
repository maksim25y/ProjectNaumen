package ru.mudan.services.integration;

import java.util.List;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import ru.mudan.domain.entity.users.Student;
import ru.mudan.domain.repositories.*;
import ru.mudan.exceptions.entity.not_found.ParentNotFoundException;
import ru.mudan.exceptions.entity.not_found.StudentNotFoundException;
import ru.mudan.exceptions.entity.not_found.SubjectNotFoundException;
import ru.mudan.services.classes.ClassService;
import ru.mudan.services.students.StudentService;
import ru.mudan.services.subjects.SubjectService;
import ru.mudan.services.users.RegistrationService;

import static org.junit.jupiter.api.Assertions.*;
import static ru.mudan.UtilConstants.*;

public class StudentServiceIT extends IntegrationTest {

    @Autowired
    private StudentRepository studentRepository;
    @Autowired
    private ClassRepository classRepository;
    @Autowired
    private SubjectsRepository subjectsRepository;
    @Autowired
    private ParentRepository parentRepository;
    @Autowired
    private StudentService studentService;
    @Autowired
    private AppUserRepository appUserRepository;
    @Autowired
    private RegistrationService registrationService;
    @Autowired
    private ClassService classService;
    @Autowired
    private SubjectService subjectService;
    @Autowired
    private TeacherRepository teacherRepository;

    private Long studentId;
    private Student studentCreated;

    @BeforeEach
    public void registerStudent() {
        var student = getDefaultRegisterUserDTOByEmail("test@mail.ru");
        registrationService.registerStudent(student);

        var studentCreated = studentRepository.findAll().getFirst();

        this.studentId = studentCreated.getId();
        this.studentCreated = studentCreated;
    }

    @AfterEach
    public void clearTables() {
        studentRepository.deleteAll();
        classRepository.deleteAll();
        subjectsRepository.deleteAll();
        parentRepository.deleteAll();
        appUserRepository.deleteAll();
        teacherRepository.deleteAll();
    }

    @Test
    public void getStudentById_existed() {
        var foundStudent = studentService.findById(studentId);

        assertAll("Grouped assertions for found student",
                () -> assertEquals(studentCreated.getId(), foundStudent.id()),
                () -> assertEquals(studentCreated.getFirstname(), foundStudent.firstname()),
                () -> assertEquals(studentCreated.getLastname(), foundStudent.lastname()),
                () -> assertEquals(studentCreated.getPatronymic(), foundStudent.patronymic()),
                () -> assertEquals(studentCreated.getEmail(), foundStudent.email()));
    }

    @Test
    public void getStudentById_notExisted() {
        assertThrows(StudentNotFoundException.class, () -> studentService.findById(studentId+1));
    }

    @Test
    public void getAllStudentsWithNotClass_notEmpty() {
        var foundStudents = studentService.findStudentsWithNotClass();

        var foundStudent = foundStudents.getFirst();

        assertAll("Grouped assertions for found students",
                () -> assertEquals(1, foundStudents.size()),
                () -> assertEquals(studentCreated.getId(), foundStudent.id()),
                () -> assertEquals(studentCreated.getFirstname(), foundStudent.firstname()),
                () -> assertEquals(studentCreated.getLastname(), foundStudent.lastname()),
                () -> assertEquals(studentCreated.getPatronymic(), foundStudent.patronymic()),
                () -> assertEquals(studentCreated.getEmail(), foundStudent.email()));
    }

    @Test
    public void getAllStudentsByClass_classExists() {
        classService.save(getDefaultClassDTO());

        var classId = classRepository.findAll().getFirst().getId();

        classService.addStudentsToClass(classId, List.of(studentId));

        var foundStudents = studentService.findAllStudentsForClass(classId);

        var foundStudent = foundStudents.getFirst();

        assertAll("Grouped assertions for found student",
                () -> assertEquals(1, foundStudents.size()),
                () -> assertEquals(studentCreated.getId(), foundStudent.id()),
                () -> assertEquals(studentCreated.getFirstname(), foundStudent.firstname()),
                () -> assertEquals(studentCreated.getLastname(), foundStudent.lastname()),
                () -> assertEquals(studentCreated.getPatronymic(), foundStudent.patronymic()),
                () -> assertEquals(studentCreated.getEmail(), foundStudent.email()));
    }

    @Test
    public void getAllStudentsByParent_parentExists() {
        registrationService.registerParent(getDefaultRegisterUserDTOByEmail("parent@mail.ru"));

        var parentId = parentRepository.findAll().getFirst().getId();
        var foundParent = parentRepository.findById(parentId).get();

        var studentById = studentRepository.findAll().getFirst();
        studentById.setParent(foundParent);
        studentRepository.save(studentById);

        var foundStudents = studentService.getAllStudentsForParent(parentId);

        var foundStudent = foundStudents.getFirst();

        assertAll("Grouped assertions for found students",
                () -> assertEquals(1, foundStudents.size()),
                () -> assertEquals(studentCreated.getId(), foundStudent.id()),
                () -> assertEquals(studentCreated.getFirstname(), foundStudent.firstname()),
                () -> assertEquals(studentCreated.getLastname(), foundStudent.lastname()),
                () -> assertEquals(studentCreated.getPatronymic(), foundStudent.patronymic()),
                () -> assertEquals(studentCreated.getEmail(), foundStudent.email()));
    }

    @Test
    public void getAllStudentsByParent_parentNotExists() {
        assertThrows(ParentNotFoundException.class, () -> studentService.getAllStudentsForParent(1L));
    }

    @Test
    public void getAllStudents_notExistedSubject() {
        assertThrows(SubjectNotFoundException.class, () -> studentService.findAllStudentsBySubjectId(1L));
    }

    @Test
    public void getAllStudentsWithNotParent_notEmpty() {
        var foundStudents = studentService.findAllStudentsWithNotParent();

        var foundStudent = foundStudents.getFirst();

        assertAll("Grouped assertions for found students",
                () -> assertEquals(1, foundStudents.size()),
                () -> assertEquals(studentCreated.getId(), foundStudent.id()),
                () -> assertEquals(studentCreated.getFirstname(), foundStudent.firstname()),
                () -> assertEquals(studentCreated.getLastname(), foundStudent.lastname()),
                () -> assertEquals(studentCreated.getPatronymic(), foundStudent.patronymic()),
                () -> assertEquals(studentCreated.getEmail(), foundStudent.email()));
    }

    @Test
    public void getAllStudentsForSubject_subjectExists() {
        classService.save(getDefaultClassDTO());

        var classId = classRepository.findAll().getFirst().getId();

        classService.addStudentsToClass(classId, List.of(studentId));

        registrationService.registerTeacher(getDefaultRegisterUserDTOByEmail("teacher@mail.ru"));

        var teacherId = teacherRepository.findAll().getFirst().getId();

        classService.addStudentsToClass(classId, List.of(studentId));

        subjectService.save(createSubjectDTOByClassIdAndTeacherId(classId, teacherId));

        var subjectId = subjectsRepository.findAll().getFirst().getId();

        var foundStudents = studentService.findAllStudentsBySubjectId(subjectId);

        var foundStudent = foundStudents.getFirst();

        assertAll("Grouped assertions for found students",
                () -> assertEquals(1, foundStudents.size()),
                () -> assertEquals(studentCreated.getId(), foundStudent.id()),
                () -> assertEquals(studentCreated.getFirstname(), foundStudent.firstname()),
                () -> assertEquals(studentCreated.getLastname(), foundStudent.lastname()),
                () -> assertEquals(studentCreated.getPatronymic(), foundStudent.patronymic()),
                () -> assertEquals(studentCreated.getEmail(), foundStudent.email()));
    }

    @Test
    public void getAllStudents_notEmpty() {
        var foundStudents = studentService.findAll();

        var foundStudent = foundStudents.getFirst();

        assertAll("Grouped assertions for found student",
                () -> assertEquals(1, foundStudents.size()),
                () -> assertEquals(studentCreated.getId(), foundStudent.id()),
                () -> assertEquals(studentCreated.getFirstname(), foundStudent.firstname()),
                () -> assertEquals(studentCreated.getLastname(), foundStudent.lastname()),
                () -> assertEquals(studentCreated.getPatronymic(), foundStudent.patronymic()),
                () -> assertEquals(studentCreated.getEmail(), foundStudent.email()));
    }

}
