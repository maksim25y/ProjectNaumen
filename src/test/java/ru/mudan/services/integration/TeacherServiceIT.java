package ru.mudan.services.integration;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import ru.mudan.domain.entity.users.Teacher;
import ru.mudan.domain.repositories.TeacherRepository;
import ru.mudan.exceptions.entity.not_found.TeacherNotFoundException;
import ru.mudan.services.auth.MyUserDetailsService;
import ru.mudan.services.teachers.TeacherService;
import ru.mudan.services.users.RegistrationService;

import static org.junit.jupiter.api.Assertions.*;
import static ru.mudan.UtilConstants.*;

public class TeacherServiceIT extends IntegrationTest {

    @Autowired
    private TeacherService teacherService;
    @Autowired
    private RegistrationService registrationService;
    @Autowired
    MyUserDetailsService myUserDetailsService;
    @Autowired
    private TeacherRepository teacherRepository;

    private Long teacherId;
    private Teacher teacherCreated;

    @BeforeEach
    public void registerTeacher() {
        var teacher = getDefaultRegisterUserDTOByEmail("test@mail.ru");
        registrationService.registerTeacher(teacher);

        var teacherCreated = teacherRepository.findAll().getFirst();

        this.teacherId = teacherCreated.getId();
        this.teacherCreated = teacherCreated;
    }

    @AfterEach
    public void clearTables() {
        myUserDetailsService.deleteUserByEmail(teacherCreated.getEmail());
    }

    @Test
    public void findTeacherByIdExisted() {
        var foundTeacher = teacherService.findTeacherById(teacherId);

        assertAll("Grouped assertions for found teacher",
                () -> assertEquals(teacherCreated.getId(), foundTeacher.id()),
                () -> assertEquals(teacherCreated.getFirstname(), foundTeacher.firstname()),
                () -> assertEquals(teacherCreated.getLastname(), foundTeacher.lastname()),
                () -> assertEquals(teacherCreated.getPatronymic(), foundTeacher.patronymic()),
                () -> assertEquals(teacherCreated.getEmail(), foundTeacher.email()));
    }

    @Test
    public void findTeacherByIdNotExisted() {
        assertThrows(TeacherNotFoundException.class, () -> teacherService.findTeacherById(teacherId+1));
    }

    @Test
    public void findAllTeachers() {
        var foundTeachers = teacherService.findAll();

        var firstTeacher = foundTeachers.getFirst();

        assertAll("Grouped assertions for found teachers",
                () -> assertEquals(1, foundTeachers.size()),
                () -> assertEquals(teacherCreated.getId(), firstTeacher.id()),
                () -> assertEquals(teacherCreated.getFirstname(), firstTeacher.firstname()),
                () -> assertEquals(teacherCreated.getLastname(), firstTeacher.lastname()),
                () -> assertEquals(teacherCreated.getPatronymic(), firstTeacher.patronymic()),
                () -> assertEquals(teacherCreated.getEmail(), firstTeacher.email()));
    }
}
