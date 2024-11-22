package ru.mudan.services.integration;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import ru.mudan.domain.repositories.*;
import ru.mudan.dto.classes.ClassDTO;
import ru.mudan.exceptions.entity.not_found.ClassEntityNotFoundException;
import ru.mudan.exceptions.entity.not_found.HomeworkNotFoundException;
import ru.mudan.exceptions.entity.not_found.SubjectNotFoundException;
import ru.mudan.services.classes.ClassService;
import ru.mudan.services.homework.HomeworkService;
import ru.mudan.services.subjects.SubjectService;
import ru.mudan.services.users.RegistrationService;

import static org.junit.jupiter.api.Assertions.*;
import static ru.mudan.UtilConstants.*;

public class HomeworkServiceIT extends IntegrationTest {

    @Autowired
    private HomeworkRepository homeworkRepository;
    @Autowired
    private ClassRepository classRepository;
    @Autowired
    private SubjectsRepository subjectsRepository;
    @Autowired
    private HomeworkService homeworkService;
    @Autowired
    private ClassService classService;
    @Autowired
    private RegistrationService registrationService;
    @Autowired
    private SubjectService subjectService;
    @Autowired
    private AppUserRepository appUserRepository;
    @Autowired
    private TeacherRepository teacherRepository;

    private Long subjectId;
    private Long classId;
    private Long teacherId;
    private Long homeworkId;

    @BeforeEach
    public void createHomework() {
        classService.save(ClassDTO
                .builder()
                .number(6)
                .letter("А")
                .description("Тестовое описание класса")
                .build());

        var classId = classRepository.findAll().getFirst().getId();
        registrationService.registerTeacher(getDefaultRegisterUserDTO());
        var teacherId = appUserRepository.findAll().getFirst().getUserId();

        var subjectForCreating = createSubjectDTOByClassIdAndTeacherId(classId, teacherId);

        subjectService.save(subjectForCreating);

        var subjectId = subjectsRepository.findAll().getFirst().getId();

        var homeworkDTO = createHomeworkCreateDTOBySubjectId(subjectId);

        homeworkService.save(homeworkDTO);

        var createdHW = homeworkRepository.findAll().getFirst();

        assertAll("Grouped assertions for created hw",
                () -> assertEquals(1, homeworkRepository.count()),
                () -> assertEquals(homeworkDTO.title(), createdHW.getTitle()),
                () -> assertEquals(homeworkDTO.description(), createdHW.getDescription()),
                () -> assertEquals(homeworkDTO.deadline(), createdHW.getDeadline()));

        this.subjectId = subjectId;
        this.classId = classId;
        this.teacherId = teacherId;
        this.homeworkId = createdHW.getId();
    }

    @AfterEach
    public void clearTables() {
        classRepository.deleteAll();
        subjectsRepository.deleteAll();
        homeworkRepository.deleteAll();
        teacherRepository.deleteAll();
        appUserRepository.deleteAll();
    }

    @Test
    public void createHomework_subjectNotExists() {
        var homeworkDTO = createHomeworkCreateDTOBySubjectId(subjectId+1);
        assertThrows(SubjectNotFoundException.class, () -> homeworkService.save(homeworkDTO));
    }

    @Test
    public void getAllHomeworksBySubjectAndClass_valid() {
        var listOfHW = homeworkService.findAllByClassAndSubject(classId, subjectId);

        assertAll("Grouped assertions for found hw",
                () -> assertNotNull(listOfHW),
                () -> assertEquals(1, listOfHW.size()));
    }

    @Test
    public void getAllHomeworksBtSubjectAndClass_subjectNotExists() {
        assertThrows(SubjectNotFoundException.class, () -> homeworkService.findAllByClassAndSubject(classId, subjectId+1));
    }

    @Test
    public void getAllHomeworksBySubjectAndClass_classNotExists() {
        assertThrows(ClassEntityNotFoundException.class, () -> homeworkService.findAllByClassAndSubject(classId+1, subjectId));
    }

    @Test
    public void getAllHomeworkByExistedClass_classExists() {
        var listOfHW = homeworkService.findAllByClass(classId);

        assertAll("Grouped assertions for found hw by class",
                () -> assertNotNull(listOfHW),
                () -> assertEquals(1, listOfHW.size()));
    }

    @Test
    public void getAllHomeworkClass_classNotExists() {
        assertThrows(ClassEntityNotFoundException.class, () ->  homeworkService.findAllByClass(classId+1));
    }

    @Test
    public void getHomeworkById_homeworkExists() {
        var foundHw = homeworkService.findById(homeworkId);
        assertNotNull(foundHw);
    }

    @Test
    public void getHomeworkById_homeworkNotExists() {
        assertThrows(HomeworkNotFoundException.class, () -> homeworkService.findById(homeworkId+1));
    }

    @Test
    public void deleteHomeworkById_homeworkExists() {
        assertEquals(1, homeworkRepository.count());
        homeworkService.delete(homeworkId);
        assertEquals(0, homeworkRepository.count());
    }

    @Test
    public void deleteHomeworkById_homeworkNotExists() {
        assertEquals(1, homeworkRepository.count());
        assertThrows(HomeworkNotFoundException.class, () -> homeworkService.delete(homeworkId+1));
        assertEquals(1, homeworkRepository.count());
    }

    @Test
    public void getAllHomeworksBySubject_subjectExists() {
        var listOfHW = homeworkService.findAllBySubject(subjectId);

        assertAll("Grouped assertions for found hw",
                () -> assertNotNull(listOfHW),
                () -> assertEquals(1, listOfHW.size()));
    }

    @Test
    public void getAllHomeworksBySubject_subjectNotExists() {
        assertThrows(SubjectNotFoundException.class, () -> homeworkService.findAllBySubject(subjectId+1));
    }

    @Test
    public void updateHomework_existed() {
        var hwForUpdate = getDefaultHomeworkDTO();

        homeworkService.update(homeworkId, hwForUpdate);

        var hwFromDBAfterUpdating = homeworkRepository.findAll().getFirst();

        assertAll("Grouped assertions for updated hw",
                () -> assertEquals(1, homeworkRepository.count()),
                () -> assertEquals(hwFromDBAfterUpdating.getDeadline(), hwForUpdate.deadline()),
                () -> assertEquals(hwFromDBAfterUpdating.getTitle(), hwForUpdate.title()),
                () -> assertEquals(hwFromDBAfterUpdating.getDescription(), hwForUpdate.description()));
    }

    @Test
    public void updateHomework_notExisted() {
        var hwForUpdate = getDefaultHomeworkDTO();

        assertThrows(HomeworkNotFoundException.class, () -> homeworkService.update(homeworkId+1, hwForUpdate));
    }
}
