package ru.mudan.services.integration;

import java.util.List;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import ru.mudan.domain.repositories.*;
import ru.mudan.dto.classes.ClassDTO;
import ru.mudan.dto.subjects.SubjectCreateDTO;
import ru.mudan.exceptions.entity.already_exists.ClassAlreadyExistsException;
import ru.mudan.exceptions.entity.not_found.ClassEntityNotFoundException;
import ru.mudan.exceptions.entity.not_found.StudentNotFoundException;
import ru.mudan.exceptions.entity.not_found.SubjectNotFoundException;
import ru.mudan.services.classes.ClassService;
import ru.mudan.services.subjects.SubjectService;
import ru.mudan.services.users.RegistrationService;

import static org.junit.jupiter.api.Assertions.*;
import static ru.mudan.UtilConstants.*;

public class ClassesServiceIT extends IntegrationTest {

    @Autowired
    private ClassService classService;
    @Autowired
    private SubjectService subjectService;
    @Autowired
    private RegistrationService registrationService;
    @Autowired
    private ClassRepository classRepository;
    @Autowired
    private StudentRepository studentRepository;
    @Autowired
    private SubjectsRepository subjectsRepository;
    @Autowired
    private AppUserRepository appUserRepository;
    @Autowired
    private TeacherRepository teacherRepository;

    @AfterEach
    public void clearTables() {
        classRepository.deleteAll();
        studentRepository.deleteAll();
        appUserRepository.deleteAll();
        subjectsRepository.deleteAll();
        teacherRepository.deleteAll();
        classRepository.deleteAll();
    }

    @Test
    public void createNewClassEntity_withNotStudents() {
        var classForSaving = getDefaultClassDTO();

        classService.save(classForSaving);

        var classFromDB = classRepository.findAll().getFirst();

        assertAll("Grouped assertions for created class",
                () -> assertEquals(1, classRepository.count()),
                () -> assertEquals(classFromDB.getNumber(), classForSaving.number()),
                () -> assertEquals(classFromDB.getLetter(), classForSaving.letter()),
                () -> assertEquals(classFromDB.getDescription(), classForSaving.description()));

    }

    @Test
    public void createNewClassEntity_withStudents() {
        var studentForAdding = getDefaultRegisterUserDTO();
        registrationService.registerStudent(studentForAdding);
        var studentId = studentRepository.findAll().getFirst().getId();

        var classForSaving = ClassDTO
                .builder()
                .id(1L)
                .number(6)
                .letter("А")
                .studentsIds(List.of(studentId))
                .description("Тестовое описание класса")
                .build();

        classService.save(classForSaving);

        var classFromDB = classRepository.findAll().getFirst();

        assertAll("Grouped assertions for created class",
                () -> assertEquals(1, classRepository.count()),
                () -> assertEquals(classFromDB.getNumber(), classForSaving.number()),
                () -> assertEquals(classFromDB.getLetter(), classForSaving.letter()),
                () -> assertEquals(classFromDB.getDescription(), classForSaving.description()),
                () -> assertNotNull(studentRepository.findById(studentId).get().getClassEntity()));
    }

    @Test
    public void createNewClassEntity_classEntityAlreadyExists() {
        var classForSaving = getDefaultClassDTO();

        classService.save(classForSaving);

        var classFromDB = classRepository.findAll().getFirst();

        assertAll("Grouped assertions for created class",
                () -> assertEquals(1, classRepository.count()),
                () -> assertEquals(classFromDB.getNumber(), classForSaving.number()),
                () -> assertEquals(classFromDB.getLetter(), classForSaving.letter()),
                () -> assertEquals(classFromDB.getDescription(), classForSaving.description()),
                () -> assertThrows(ClassAlreadyExistsException.class, () -> classService.save(classForSaving)));
    }

    @Test
    public void updateClassEntity_valid() {
        var classForSaving = getDefaultClassDTO();

        classService.save(classForSaving);
        var classFromDB = classRepository.findAll().getFirst();

        assertAll("Grouped assertions for created class",
                () -> assertEquals(1, classRepository.count()),
                () -> assertEquals(classFromDB.getNumber(), classForSaving.number()),
                () -> assertEquals(classFromDB.getLetter(), classForSaving.letter()),
                () -> assertThrows(ClassAlreadyExistsException.class, () -> classService.save(classForSaving)));

        var classForUpdating = ClassDTO
                .builder()
                .number(8)
                .letter("Б")
                .description("Новое описание класса")
                .build();

        var idForCreatedClass = classFromDB.getId();

        classService.update(classForUpdating, idForCreatedClass);
        var updatedClassFromDB = classRepository.findAll().getFirst();

        assertAll("Grouped assertions for created class",
                () -> assertEquals(1, classRepository.count()),
                () -> assertEquals(updatedClassFromDB.getNumber(), classForUpdating.number()),
                () -> assertEquals(updatedClassFromDB.getLetter(), classForUpdating.letter()),
                () -> assertEquals(updatedClassFromDB.getDescription(), classForUpdating.description()));
    }

    @Test
    public void updateClassEntity_classAlreadyExists() {
        var classForSaving = getDefaultClassDTO();

        classService.save(classForSaving);

        var secondClassForCreating = ClassDTO
                .builder()
                .number(8)
                .letter("Б")
                .description("Новое описание класса")
                .build();

        classService.save(secondClassForCreating);
        var idOfSecondClass = classRepository.findByLetterAndNumber(secondClassForCreating.letter(), secondClassForCreating.number()).get().getId();

        var classForUpdating = ClassDTO
                .builder()
                .number(classForSaving.number())
                .letter(classForSaving.letter())
                .description("Новое описание класса")
                .build();

        assertThrows(ClassAlreadyExistsException.class, () -> classService.update(classForUpdating, idOfSecondClass));
    }

    @Test
    public void updateClassEntityWithSubjects_classExisted() {
        var classForSaving = getDefaultClassDTO();

        classService.save(classForSaving);
        var classFromDB = classRepository.findAll().getFirst();

        assertAll("Grouped assertions for created class",
                () -> assertEquals(1, classRepository.count()),
                () -> assertEquals(classFromDB.getNumber(), classForSaving.number()),
                () -> assertEquals(classFromDB.getLetter(), classForSaving.letter()),
                () -> assertThrows(ClassAlreadyExistsException.class, () -> classService.save(classForSaving)));

        var idForCreatedClass = classFromDB.getId();

        registrationService.registerTeacher(getDefaultRegisterUserDTO());
        var idOfTeacher = appUserRepository.findAll().getFirst().getUserId();

        var subjectForCreating = SubjectCreateDTO
                .builder()
                .name("Математика")
                .type("Базовый")
                .description("Тестовое описание")
                .classId(idForCreatedClass)
                .teacherId(idOfTeacher)
                .build();


        subjectService.save(subjectForCreating);

        var idOfCreatedSubject = subjectsRepository.findAll().getFirst().getId();

        var classForUpdating = ClassDTO
                .builder()
                .number(8)
                .letter("Б")
                .description("Новое описание класса")
                .subjectsIds(List.of(idOfCreatedSubject))
                .build();

        classService.update(classForUpdating, idForCreatedClass);
        var updatedClassFromDB = classRepository.findAll().getFirst();

        assertAll("Grouped assertions for created class",
                () -> assertEquals(1, classRepository.count()),
                () -> assertEquals(updatedClassFromDB.getNumber(), classForUpdating.number()),
                () -> assertEquals(updatedClassFromDB.getLetter(), classForUpdating.letter()),
                () -> assertEquals(updatedClassFromDB.getDescription(), classForUpdating.description()),
                () -> assertNotNull(updatedClassFromDB.getSubjects()));
    }

    @Test
    public void deleteClassById_classExisted() {
        var classForSaving = getDefaultClassDTO();

        classService.save(classForSaving);
        var classFromDB = classRepository.findAll().getFirst();

        assertAll("Grouped assertions for created class",
                () -> assertEquals(1, classRepository.count()),
                () -> assertEquals(classFromDB.getNumber(), classForSaving.number()),
                () -> assertEquals(classFromDB.getLetter(), classForSaving.letter()));

        var idForCreatedClass = classFromDB.getId();

        classService.deleteById(idForCreatedClass);

        assertAll("Grouped assertions for deleted class",
                () -> assertEquals(0, classRepository.count()));
    }

    @Test
    public void deleteClassById_classNotExisted() {
         assertThrows(ClassEntityNotFoundException.class, () -> classService.deleteById(9L));
    }

    @Test
    public void getClassById_classExisted() {
        var classForSaving = getDefaultClassDTO();

        classService.save(classForSaving);
        var classFromDB = classRepository.findAll().getFirst();
        var idForCreatedClass = classFromDB.getId();

        var foundClass = classService.findById(idForCreatedClass);

        assertAll("Grouped assertions for created class",
                () -> assertEquals(classForSaving.number(), foundClass.number()),
                () -> assertEquals(classForSaving.letter(), foundClass.letter()),
                () -> assertEquals(classForSaving.description(), foundClass.description()));
    }

    @Test
    public void getClassById_classNotExisted() {
        assertThrows(ClassEntityNotFoundException.class, () -> classService.findById(9L));
    }

    @Test
    public void getAllClasses_notEmpty() {
        var classForSaving = getDefaultClassDTO();

        classService.save(classForSaving);

        var foundClasses = classService.findAll();

        assertEquals(1, foundClasses.size());
    }

    @Test
    public void addStudentsToClass_classExisted() {
        var studentForRegistering = getDefaultRegisterUserDTO();
        registrationService.registerStudent(studentForRegistering);
        var idOfStudent = appUserRepository.findAll().getFirst().getUserId();

        var classForSaving = getDefaultClassDTO();
        classService.save(classForSaving);

        var classFromDBId = classRepository.findAll().getFirst().getId();

        classService.addStudentsToClass(classFromDBId, List.of(idOfStudent));

        var studentsForClass = classRepository.findAll().getFirst().getStudents();

        assertEquals(1, studentsForClass.size());
    }

    @Test
    public void addStudentsToClass_classNotExisted() {
        var classForSaving = getDefaultClassDTO();
        classService.save(classForSaving);

        var classFromDBId = classRepository.findAll().getFirst().getId();

        assertThrows(StudentNotFoundException.class, () -> classService.addStudentsToClass(classFromDBId, List.of(10L)));
    }

    @Test
    public void addSubjectsToClass_classExisted() {
        var teacherForRegistering = getDefaultRegisterUserDTO();
        registrationService.registerTeacher(teacherForRegistering);
        var idOfTeacher = appUserRepository.findAll().getFirst().getUserId();

        var classForSaving = getDefaultClassDTO();
        classService.save(classForSaving);

        var classFromDBId = classRepository.findAll().getFirst().getId();

        var subjectForCreating = SubjectCreateDTO
                .builder()
                .name("Математика")
                .type("Базовый")
                .description("Тестовое описание")
                .classId(classFromDBId)
                .teacherId(idOfTeacher)
                .build();

        subjectService.save(subjectForCreating);

        var idOfCreatedSubject = subjectsRepository.findAll().getFirst().getId();

        classService.addSubjectsToClass(classFromDBId, List.of(idOfCreatedSubject));

        var subjectsForClass = classRepository.findAll().getFirst().getSubjects();

        assertEquals(1,  subjectsForClass.size());
    }

    @Test
    public void addSubjectsToClass_classNotExisted() {
        var classForSaving = getDefaultClassDTO();
        classService.save(classForSaving);

        var classFromDBId = classRepository.findAll().getFirst().getId();

        assertThrows(SubjectNotFoundException.class, () -> classService.addSubjectsToClass(classFromDBId, List.of(10L)));
    }

}
