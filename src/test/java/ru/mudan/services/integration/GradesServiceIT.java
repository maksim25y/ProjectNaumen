package ru.mudan.services.integration;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import ru.mudan.domain.repositories.*;
import ru.mudan.dto.classes.ClassDTO;
import ru.mudan.dto.grades.GradeDTO;
import ru.mudan.exceptions.entity.not_found.GradeNotFoundException;
import ru.mudan.exceptions.entity.not_found.StudentNotFoundException;
import ru.mudan.exceptions.entity.not_found.SubjectNotFoundException;
import ru.mudan.services.classes.ClassService;
import ru.mudan.services.grades.GradesService;
import ru.mudan.services.subjects.SubjectService;
import ru.mudan.services.users.RegistrationService;

import static java.time.LocalDate.now;
import static org.junit.jupiter.api.Assertions.*;
import static ru.mudan.UtilConstants.*;

public class GradesServiceIT extends IntegrationTest {

    @Autowired
    private GradeRepository gradeRepository;
    @Autowired
    private SubjectsRepository subjectsRepository;
    @Autowired
    private ClassService classService;
    @Autowired
    private RegistrationService registrationService;
    @Autowired
    private SubjectService subjectService;
    @Autowired
    private AppUserRepository appUserRepository;
    @Autowired
    private StudentRepository studentRepository;
    @Autowired
    private ClassRepository classRepository;
    @Autowired
    private TeacherRepository teacherRepository;
    @Autowired
    private GradesService gradesService;

    private GradeDTO createdGrade;
    private Long subjectId;
    private Long studentId;
    private Long gradeId;

    @BeforeEach
    public void createHomework() {
        classService.save(ClassDTO
                .builder()
                .number(6)
                .letter("А")
                .description("Тестовое описание класса")
                .build());

        var classId = classRepository.findAll().getFirst().getId();
        registrationService.registerStudent(getDefaultRegisterUserDTOByEmail("test1@mail.ru"));
        registrationService.registerTeacher(getDefaultRegisterUserDTOByEmail("test2@mail.ru"));
        var studentId = studentRepository.findAll().getFirst().getId();
        var teacherId = teacherRepository.findAll().getFirst().getId();

        var subjectForCreating = createSubjectDTOByClassIdAndTeacherId(classId, teacherId);

        subjectService.save(subjectForCreating);

        var subjectId = subjectsRepository.findAll().getFirst().getId();

        var gradeCreateDTO = createGradeDTOByStudentIdAndSubjectId(studentId, subjectId);

        gradesService.save(gradeCreateDTO);

        var createdGrade = gradeRepository.findAll().getFirst();

        this.subjectId = subjectId;
        this.studentId = studentId;
        this.gradeId = createdGrade.getId();
        this.createdGrade = gradeCreateDTO;
    }

    @AfterEach
    public void clearTables() {
        gradeRepository.deleteAll();
        classRepository.deleteAll();
        subjectsRepository.deleteAll();
        studentRepository.deleteAll();
        appUserRepository.deleteAll();
        teacherRepository.deleteAll();
    }

    @Test
    public void createGradeTest() {
        assertEquals(1, gradeRepository.findAll().size());
    }

    @Test
    public void createGradeWithNotExistedSubject() {
        var gradeCreateDTO = createGradeDTOByStudentIdAndSubjectId(studentId, subjectId+1);

        assertThrows(SubjectNotFoundException.class, () -> gradesService.save(gradeCreateDTO));
    }

    @Test
    public void createGradeWithNotExistedStudent() {
        var gradeCreateDTO = createGradeDTOByStudentIdAndSubjectId(studentId+1, subjectId);

        assertThrows(StudentNotFoundException.class, () -> gradesService.save(gradeCreateDTO));
    }

    @Test
    public void getExistedGradeById() {
        var foundGrade = gradesService.findById(gradeId);

        assertAll("Grouped assertions for found grade",
                () -> assertNotNull(foundGrade),
                () -> assertEquals(createdGrade.mark(), foundGrade.mark()),
                () -> assertEquals(createdGrade.comment(), foundGrade.comment()),
                () -> assertEquals(createdGrade.dateOfMark(), foundGrade.dateOfMark()));
    }

    @Test
    public void getNotExistedGradeById() {
        assertThrows(GradeNotFoundException.class, () -> gradesService.findById(gradeId+1));
    }

    @Test
    public void getAllGradesForStudentBySubject() {
        var foundGrades = gradesService.findAllGradesForStudentWithSubject(studentId, subjectId);

        var grade = foundGrades.getFirst();

        assertAll("Grouped assertions for found grades",
                () -> assertEquals(1, foundGrades.size()),
                () -> assertEquals(createdGrade.mark(), grade.mark()),
                () -> assertEquals(createdGrade.comment(), grade.comment()),
                () -> assertEquals(createdGrade.dateOfMark(), grade.dateOfMark()));
    }

    @Test
    public void getAllGradesForNotExistedStudentBySubject() {
        assertThrows(StudentNotFoundException.class, () -> gradesService.findAllGradesForStudentWithSubject(studentId+1, subjectId));
    }

    @Test
    public void getAllGradesForStudentByNotExistedSubject() {
        assertThrows(SubjectNotFoundException.class, () -> gradesService.findAllGradesForStudentWithSubject(studentId, subjectId+1));
    }

    @Test
    public void getAllGradesForExistedStudent() {
        var foundGrades = gradesService.findAllGradesForStudent(studentId);

        var grade = foundGrades.getFirst();

        assertAll("Grouped assertions for found grades",
                () -> assertEquals(1, foundGrades.size()),
                () -> assertEquals(createdGrade.mark(), grade.mark()),
                () -> assertEquals(createdGrade.comment(), grade.comment()),
                () -> assertEquals(createdGrade.dateOfMark(), grade.dateOfMark()));
    }

    @Test
    public void getAllGradesForNotExistedStudent() {
        assertThrows(StudentNotFoundException.class, () -> gradesService.findAllGradesForStudent(studentId+1));
    }

    @Test
    public void getAllGradesBySubject() {
        var foundGrades = gradesService.findAllBySubjectId(subjectId);

        var grade = foundGrades.getFirst();

        assertAll("Grouped assertions for found grades",
                () -> assertEquals(1, foundGrades.size()),
                () -> assertEquals(createdGrade.mark(), grade.mark()),
                () -> assertEquals(createdGrade.comment(), grade.comment()),
                () -> assertEquals(createdGrade.dateOfMark(), grade.dateOfMark()));
    }

    @Test
    public void getAllGradesByBotExistedSubject() {
        assertThrows(SubjectNotFoundException.class, () -> gradesService.findAllBySubjectId(subjectId+1));
    }

    @Test
    public void updateExistedGradeById() {
        var gradeForUpdate = GradeDTO
                .builder()
                .mark(3)
                .subjectId(subjectId)
                .comment("Не очень")
                .dateOfMark(now())
                .studentId(studentId)
                .build();

        gradesService.update(gradeForUpdate, gradeId);

        var foundGrade = gradesService.findById(gradeId);

        assertAll("Grouped assertions for found grade",
                () -> assertNotNull(foundGrade),
                () -> assertEquals(gradeForUpdate.mark(), foundGrade.mark()),
                () -> assertEquals(gradeForUpdate.comment(), foundGrade.comment()),
                () -> assertEquals(gradeForUpdate.dateOfMark(), foundGrade.dateOfMark()));
    }

    @Test
    public void updateNotExistedGradeById() {
        var gradeForUpdate = GradeDTO
                .builder()
                .mark(3)
                .subjectId(subjectId)
                .comment("Не очень")
                .dateOfMark(now())
                .studentId(studentId)
                .build();

        assertThrows(GradeNotFoundException.class, () -> gradesService.update(gradeForUpdate, gradeId+1));
    }

    @Test
    public void deleteExistedGradeById() {
        gradesService.deleteById(gradeId);
        assertEquals(0, gradeRepository.count());
    }

    @Test
    public void deleteNotExistedGradeById() {
        assertThrows(GradeNotFoundException.class, () -> gradesService.deleteById(gradeId+1));
    }

}
