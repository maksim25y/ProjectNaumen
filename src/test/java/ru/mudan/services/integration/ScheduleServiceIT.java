package ru.mudan.services.integration;

import java.time.LocalTime;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import ru.mudan.domain.repositories.*;
import ru.mudan.dto.classes.ClassDTO;
import ru.mudan.dto.schedule.ScheduleCreateDTO;
import ru.mudan.dto.schedule.ScheduleUpdateDTO;
import ru.mudan.exceptions.entity.not_found.ClassEntityNotFoundException;
import ru.mudan.exceptions.entity.not_found.ScheduleNotFoundException;
import ru.mudan.exceptions.entity.not_found.SubjectNotFoundException;
import ru.mudan.services.classes.ClassService;
import ru.mudan.services.schedule.ScheduleService;
import ru.mudan.services.subjects.SubjectService;
import ru.mudan.services.users.RegistrationService;
import ru.mudan.util.enums.DayOfWeek;

import static org.junit.jupiter.api.Assertions.*;
import static ru.mudan.UtilConstants.*;

public class ScheduleServiceIT extends IntegrationTest {

    @Autowired
    private ScheduleRepository scheduleRepository;
    @Autowired
    private ClassRepository classRepository;
    @Autowired
    private SubjectsRepository subjectsRepository;
    @Autowired
    private ScheduleService scheduleService;
    @Autowired
    private ClassService classService;
    @Autowired
    private SubjectService subjectService;
    @Autowired
    private RegistrationService registrationService;
    @Autowired
    private AppUserRepository appUserRepository;
    @Autowired
    private TeacherRepository teacherRepository;

    private Long classId;
    private Long teacherId;
    private Long subjectId;
    private Long scheduleId;
    private ScheduleCreateDTO scheduleCreateDTO;

    @BeforeEach
    public void createSchedule() {
        classService.save(ClassDTO
                .builder()
                .number(6)
                .letter("А")
                .description("Тестовое описание класса")
                .build());

        this.classId = classRepository.findAll().getFirst().getId();

        registrationService.registerTeacher(getDefaultRegisterUserDTO());

        this.teacherId = teacherRepository.findAll().getFirst().getId();

        subjectService.save(createSubjectDTOByClassIdAndTeacherId(classId, teacherId));

        this.subjectId = subjectsRepository.findAll().getFirst().getId();

        this.scheduleCreateDTO = createScheduleCreateDTOBySubjectIdAndClassId(subjectId, classId);
        scheduleService.save(scheduleCreateDTO);
        this.scheduleId = scheduleRepository.findAll().getFirst().getId();
    }

    @AfterEach
    public void clearTables() {
        scheduleRepository.deleteAll();
        classRepository.deleteAll();
        subjectsRepository.deleteAll();
        teacherRepository.deleteAll();
        appUserRepository.deleteAll();
    }

    @Test
    public void createScheduleTest() {
        assertEquals(1, scheduleRepository.findAll().size());
    }

    @Test
    public void createSchedule_subjectNotExists() {
        var scheduleCreateDTOWithNotExistedSubjectId = createScheduleCreateDTOBySubjectIdAndClassId(subjectId+1, classId);
        assertThrows(SubjectNotFoundException.class, () -> scheduleService.save(scheduleCreateDTOWithNotExistedSubjectId));
    }

    @Test
    public void createSchedule_classNotExists() {
        var scheduleCreateDTOWithNotExistedClassId = createScheduleCreateDTOBySubjectIdAndClassId(subjectId, classId+1);
        assertThrows(ClassEntityNotFoundException.class, () -> scheduleService.save(scheduleCreateDTOWithNotExistedClassId));
    }

    @Test
    public void getSchedules_subjectExists() {
        var schedulesBySubject = scheduleService.findAllBySubjectId(subjectId);

        var scheduleFirst = scheduleRepository.findAll().getFirst();
        assertAll("Grouped assertions for found schedules",
                () -> assertEquals(1, schedulesBySubject.size()),
                () -> assertEquals(scheduleCreateDTO.dayOfWeek(), scheduleFirst.getDayOfWeek()),
                () -> assertEquals(scheduleCreateDTO.numberOfClassroom(), scheduleFirst.getNumberOfClassroom()));
    }

    @Test
    public void getSchedules_subjectNotExists() {
        assertThrows(SubjectNotFoundException.class, () -> scheduleService.findAllBySubjectId(subjectId+1));
    }

    @Test
    public void getSchedulesClass_classExists() {
        var schedulesBySubject = scheduleService.findAllSchedulesForClass(classId);

        var scheduleFirst = scheduleRepository.findAll().getFirst();
        assertAll("Grouped assertions for found schedules",
                () -> assertEquals(1, schedulesBySubject.size()),
                () -> assertEquals(scheduleCreateDTO.dayOfWeek(), scheduleFirst.getDayOfWeek()),
                () -> assertEquals(scheduleCreateDTO.numberOfClassroom(), scheduleFirst.getNumberOfClassroom()));
    }

    @Test
    public void getSchedulesClass_classNotExists() {
        assertThrows(ClassEntityNotFoundException.class, () -> scheduleService.findAllSchedulesForClass(subjectId+1));
    }

    @Test
    public void updateSchedule_existed() {
        var scheduleForUpdate = ScheduleUpdateDTO
                .builder()
                .startTime(LocalTime.now())
                .dayOfWeek(2)
                .numberOfClassroom(100)
                .build();

        scheduleService.update(scheduleForUpdate, scheduleId);

        var foundSchedule = scheduleRepository.findAll().getFirst();

        assertAll("Grouped assertions for updated schedule",
                () -> assertEquals(1, scheduleRepository.count()),
                () -> assertEquals(scheduleForUpdate.dayOfWeek(), foundSchedule.getDayOfWeek()),
                () -> assertEquals(scheduleForUpdate.numberOfClassroom(), foundSchedule.getNumberOfClassroom()));
    }

    @Test
    public void updateSchedule_notExisted() {
        var scheduleForUpdate = ScheduleUpdateDTO
                .builder()
                .startTime(LocalTime.now())
                .dayOfWeek(2)
                .numberOfClassroom(100)
                .build();

        assertThrows(ScheduleNotFoundException.class, () -> scheduleService.update(scheduleForUpdate, scheduleId+1));
    }

    @Test
    public void deleteSchedule_existed() {
        scheduleService.deleteById(scheduleId);

        assertEquals(0, scheduleRepository.count());
    }

    @Test
    public void deleteSchedule_notExisted() {
        assertThrows(ScheduleNotFoundException.class, () -> scheduleService.deleteById(scheduleId+1));
    }

    @Test
    public void getScheduleById_existed() {
        var scheduleBySubject = scheduleService.findById(scheduleId);

        assertAll("Grouped assertions for found schedule",
                () -> assertEquals(DayOfWeek.getDayOfWeekByNumber(scheduleCreateDTO.dayOfWeek()).getName(), scheduleBySubject.dayOfWeek()),
                () -> assertEquals(scheduleCreateDTO.numberOfClassroom(), scheduleBySubject.numberOfClassRoom()));
    }

    @Test
    public void getScheduleById_notExisted() {
        assertThrows(ScheduleNotFoundException.class, () -> scheduleService.findById(scheduleId+1));
    }
}
