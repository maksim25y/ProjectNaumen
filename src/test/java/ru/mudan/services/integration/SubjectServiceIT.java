package ru.mudan.services.integration;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import ru.mudan.domain.repositories.AppUserRepository;
import ru.mudan.domain.repositories.ClassRepository;
import ru.mudan.domain.repositories.SubjectsRepository;
import ru.mudan.domain.repositories.TeacherRepository;
import ru.mudan.dto.classes.ClassDTO;
import ru.mudan.dto.subjects.SubjectUpdateDTO;
import ru.mudan.exceptions.entity.already_exists.SubjectAlreadyExistsException;
import ru.mudan.exceptions.entity.not_found.ClassEntityNotFoundException;
import ru.mudan.exceptions.entity.not_found.SubjectNotFoundException;
import ru.mudan.exceptions.entity.not_found.TeacherNotFoundException;
import ru.mudan.services.classes.ClassService;
import ru.mudan.services.subjects.SubjectService;
import ru.mudan.services.users.RegistrationService;

import static org.junit.jupiter.api.Assertions.*;
import static ru.mudan.UtilConstants.*;

public class SubjectServiceIT extends IntegrationTest {

    @Autowired
    private TeacherRepository teacherRepository;
    @Autowired
    private SubjectsRepository subjectsRepository;
    @Autowired
    private ClassRepository classRepository;
    @Autowired
    private SubjectService subjectService;
    @Autowired
    private ClassService classService;
    @Autowired
    private RegistrationService registrationService;
    @Autowired
    private AppUserRepository appUserRepository;

    private Long classId;
    private Long teacherId;

    @BeforeEach
    public void createClass() {
        classService.save(ClassDTO
                .builder()
                .number(6)
                .letter("А")
                .description("Тестовое описание класса")
                .build());

        registrationService.registerTeacher(getDefaultRegisterUserDTO());

        this.classId = classRepository.findAll().getFirst().getId();
        this.teacherId = teacherRepository.findAll().getFirst().getId();
    }

    @AfterEach
    public void clearTables() {
        classRepository.deleteAll();
        subjectsRepository.deleteAll();
        teacherRepository.deleteAll();
        appUserRepository.deleteAll();
    }

    @Test
    public void createNewSubject_valid() {
        var subjectForCreating = createSubjectDTOByClassIdAndTeacherId(classId, teacherId);

        subjectService.save(subjectForCreating);

        var foundSubject = subjectsRepository.findAll().getFirst();

        assertAll("Grouped assertions for created",
                () -> assertEquals(subjectForCreating.name(), foundSubject.getName()),
                () -> assertEquals(subjectForCreating.description(), foundSubject.getDescription()),
                () -> assertEquals(subjectForCreating.type(), foundSubject.getType()));
    }

    @Test
    public void getAllSubjects_notEmpty() {
        var subjectForCreating = createSubjectDTOByClassIdAndTeacherId(classId, teacherId);

        subjectService.save(subjectForCreating);

        var foundSubjects = subjectService.findAll();


        assertAll("Grouped assertions for found subjects",
                () -> assertEquals(1, foundSubjects.size()),
                () -> assertEquals(subjectForCreating.description(), foundSubjects.getFirst().description()),
                () -> assertEquals(subjectForCreating.name(), foundSubjects.getFirst().name()),
                () -> assertEquals(subjectForCreating.type(), foundSubjects.getFirst().type()),
                () -> assertNotNull(foundSubjects.getFirst().id()));
    }

    @Test
    public void getSubjectById_existed() {
        var subjectForCreating = createSubjectDTOByClassIdAndTeacherId(classId, teacherId);

        subjectService.save(subjectForCreating);

        var createdSubjectFromDB = subjectsRepository.findAll().getFirst();

        var foundSubject = subjectService.findById(createdSubjectFromDB.getId());


        assertAll("Grouped assertions for found subject",
                () -> assertEquals(subjectForCreating.description(), foundSubject.description()),
                () -> assertEquals(subjectForCreating.name(), foundSubject.name()),
                () -> assertEquals(subjectForCreating.type(), foundSubject.type()),
                () -> assertNotNull(foundSubject.id()));
    }

    @Test
    public void getSubjectById_notExisted() {
        assertThrows(SubjectNotFoundException.class, () -> subjectService.findById(1L));
    }

    @Test
    public void deleteSubjectById_existed() {
        var subjectForCreating = createSubjectDTOByClassIdAndTeacherId(classId, teacherId);

        subjectService.save(subjectForCreating);

        var foundSubjectId = subjectsRepository.findAll().getFirst().getId();

        subjectService.deleteById(foundSubjectId);

        assertAll("Grouped assertions for deleted subject",
                () -> assertEquals(0, subjectsRepository.count()),
                () -> assertThrows(SubjectNotFoundException.class, () -> subjectService.findById(foundSubjectId)));
    }

    @Test
    public void deleteSubjectById_notExisted() {
        assertThrows(SubjectNotFoundException.class, () -> subjectService.deleteById(1L));
    }

    @Test
    public void getAllSubjectsByClassId_classExists() {
        var subjectForCreating = createSubjectDTOByClassIdAndTeacherId(classId, teacherId);

        subjectService.save(subjectForCreating);

        var foundSubjects = subjectService.findAllSubjectsForClass(classId);


        assertAll("Grouped assertions for found subjects",
                () -> assertEquals(1, foundSubjects.size()),
                () -> assertEquals(subjectForCreating.description(), foundSubjects.getFirst().description()),
                () -> assertEquals(subjectForCreating.name(), foundSubjects.getFirst().name()),
                () -> assertEquals(subjectForCreating.type(), foundSubjects.getFirst().type()),
                () -> assertNotNull(foundSubjects.getFirst().id()));
    }

    @Test
    public void getAllSubjectsByClassId_classNotExists() {
        assertThrows(ClassEntityNotFoundException.class, () -> subjectService.findAllSubjectsForClass(classId+1));
    }

    @Test
    public void getAllSubjectsByTeacherId_teacherExists() {
        var subjectForCreating = createSubjectDTOByClassIdAndTeacherId(classId, teacherId);

        subjectService.save(subjectForCreating);

        var foundSubjects = subjectService.getSubjectsForTeacher(teacherId);


        assertAll("Grouped assertions for found subjects",
                () -> assertEquals(1, foundSubjects.size()),
                () -> assertEquals(subjectForCreating.description(), foundSubjects.getFirst().description()),
                () -> assertEquals(subjectForCreating.name(), foundSubjects.getFirst().name()),
                () -> assertEquals(subjectForCreating.type(), foundSubjects.getFirst().type()),
                () -> assertNotNull(foundSubjects.getFirst().id()));
    }

    @Test
    public void getAllSubjectsBydId_teacherNotExists() {
        assertThrows(TeacherNotFoundException.class, () -> subjectService.getSubjectsForTeacher(teacherId+1));
    }

    @Test
    public void updateSubject_existed() {
        var subjectForCreating = createSubjectDTOByClassIdAndTeacherId(classId, teacherId);

        subjectService.save(subjectForCreating);

        var foundSubjectId = subjectsRepository.findAll().getFirst().getId();

        var subjectUpdateDTO = SubjectUpdateDTO
                .builder()
                .type("Факультативный")
                .description("Новое описание")
                .build();

        subjectService.update(subjectUpdateDTO, foundSubjectId);

        var foundSubjectFromDB = subjectsRepository.findAll().getFirst();

        assertAll("Grouped assertions for updated subject",
                () -> assertEquals(1, subjectsRepository.count()),
                () -> assertEquals(subjectUpdateDTO.type(), foundSubjectFromDB.getType()),
                () -> assertEquals(subjectUpdateDTO.description(), foundSubjectFromDB.getDescription()));
    }

    @Test
    public void updateSubject_notExisted() {
        var subjectUpdateDTO = SubjectUpdateDTO
                .builder()
                .type("Факультативный")
                .description("Новое описание")
                .build();

        assertThrows(SubjectNotFoundException.class, () -> subjectService.update(subjectUpdateDTO, 1L));
    }

    @Test
    public void createSubject_classNotExists() {
        var subjectForCreating = createSubjectDTOByClassIdAndTeacherId(classId+1, teacherId);

        assertThrows(ClassEntityNotFoundException.class, () -> subjectService.save(subjectForCreating));
    }

    @Test
    public void createSubject_teacherNotExists() {
        var subjectForCreating = createSubjectDTOByClassIdAndTeacherId(classId, teacherId+1);

        assertThrows(TeacherNotFoundException.class, () -> subjectService.save(subjectForCreating));
    }

    @Test
    public void createSubject_subjectAlreadyExists() {
        var subjectForCreating = createSubjectDTOByClassIdAndTeacherId(classId, teacherId);

        subjectService.save(subjectForCreating);

        assertThrows(SubjectAlreadyExistsException.class, () -> subjectService.save(subjectForCreating));
    }

}
