package ru.mudan.services.integration;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import ru.mudan.domain.repositories.*;
import ru.mudan.exceptions.entity.not_found.UserNotFoundException;
import ru.mudan.services.auth.MyUserDetailsService;
import ru.mudan.services.users.RegistrationService;

import static org.junit.jupiter.api.Assertions.*;
import static ru.mudan.UtilConstants.*;

public class MyUserDetailsServiceIT extends IntegrationTest {

    @Autowired
    private MyUserDetailsService myUserDetailsService;
    @Autowired
    private RegistrationService registrationService;
    @Autowired
    private TeacherRepository teacherRepository;
    @Autowired
    private ParentRepository parentRepository;
    @Autowired
    private StudentRepository studentRepository;
    @Autowired
    private AdminRepository adminRepository;
    @Autowired
    private AppUserRepository appUserRepository;

    @AfterEach
    public void clearTables() {
        parentRepository.deleteAll();
        studentRepository.deleteAll();
        teacherRepository.deleteAll();
        adminRepository.deleteAll();
        appUserRepository.deleteAll();
    }

    @Test
    public void updateTeacherByExistedEmail() {
        var teacherForCreating = getDefaultRegisterUserDTOByEmail("test@mail.ru");
        registrationService.registerTeacher(teacherForCreating);

        var teacherCreated = teacherRepository.findAll().getFirst();

        var teacherId = teacherCreated.getId();

        assertAll("Grouped assertions for created teacher",
                () -> assertEquals(teacherCreated.getFirstname(), teacherForCreating.firstname()),
                () -> assertEquals(teacherCreated.getLastname(), teacherForCreating.lastname()),
                () -> assertEquals(teacherCreated.getPatronymic(), teacherForCreating.patronymic()),
                () -> assertEquals(teacherCreated.getEmail(), teacherForCreating.email()));

        var teacherForUpdating = getDefaultUserUpdateDTO();

        myUserDetailsService.updateUserByEmail(teacherForCreating.email(), teacherForUpdating);

        var teacherUpdated = teacherRepository.findById(teacherId).get();

        assertAll("Grouped assertions for updated teacher",
                () -> assertEquals(teacherForUpdating.firstname(), teacherUpdated.getFirstname()),
                () -> assertEquals(teacherForUpdating.lastname(), teacherUpdated.getLastname()),
                () -> assertEquals(teacherForUpdating.patronymic(), teacherUpdated.getPatronymic()));
    }

    @Test
    public void updateTeacherByNotExistedEmail() {
        var teacherForCreating = getDefaultRegisterUserDTOByEmail("test@mail.ru");
        registrationService.registerTeacher(teacherForCreating);

        var teacherCreated = teacherRepository.findAll().getFirst();

        assertAll("Grouped assertions for created teacher",
                () -> assertEquals(teacherForCreating.firstname(), teacherCreated.getFirstname()),
                () -> assertEquals(teacherForCreating.lastname(), teacherCreated.getLastname()),
                () -> assertEquals(teacherForCreating.patronymic(), teacherCreated.getPatronymic()),
                () -> assertEquals(teacherForCreating.email(), teacherCreated.getEmail()));

        var teacherForUpdating = getDefaultUserUpdateDTO();

        assertThrows(UserNotFoundException.class, () -> myUserDetailsService.updateUserByEmail("not_existed@mail.ru", teacherForUpdating));
    }

    @Test
    public void updateStudentByExistedEmail() {
        var studentForCreating = getDefaultRegisterUserDTOByEmail("test@mail.ru");
        registrationService.registerStudent(studentForCreating);

        var studentCreated = studentRepository.findAll().getFirst();

        var studentId = studentCreated.getId();

        assertAll("Grouped assertions for created student",
                () -> assertEquals(studentForCreating.firstname(), studentCreated.getFirstname()),
                () -> assertEquals(studentForCreating.lastname(), studentCreated.getLastname()),
                () -> assertEquals(studentForCreating.patronymic(), studentCreated.getPatronymic()),
                () -> assertEquals(studentForCreating.email(), studentCreated.getEmail()));

        var studentForUpdating = getDefaultUserUpdateDTO();

        myUserDetailsService.updateUserByEmail(studentForCreating.email(), studentForUpdating);

        var studentUpdated = studentRepository.findById(studentId).get();

        assertAll("Grouped assertions for updated student",
                () -> assertEquals(studentForUpdating.firstname(), studentUpdated.getFirstname()),
                () -> assertEquals(studentForUpdating.lastname(), studentUpdated.getLastname()),
                () -> assertEquals(studentForUpdating.patronymic(), studentUpdated.getPatronymic()));
    }

    @Test
    public void updateStudentByNotExistedEmail() {
        var studentForCreating = getDefaultRegisterUserDTOByEmail("test@mail.ru");
        registrationService.registerStudent(studentForCreating);

        var studentCreated = studentRepository.findAll().getFirst();

        assertAll("Grouped assertions for created student",
                () -> assertEquals(studentForCreating.firstname(), studentCreated.getFirstname()),
                () -> assertEquals(studentForCreating.lastname(), studentCreated.getLastname()),
                () -> assertEquals(studentForCreating.patronymic(), studentCreated.getPatronymic()),
                () -> assertEquals(studentForCreating.email(), studentCreated.getEmail()));

        var studentForUpdating = getDefaultUserUpdateDTO();

        assertThrows(UserNotFoundException.class, () -> myUserDetailsService.updateUserByEmail("not_existed@mail.ru", studentForUpdating));
    }

    @Test
    public void updateParentByExistedEmail() {
        var parentForCreating = getDefaultRegisterUserDTOByEmail("test@mail.ru");
        registrationService.registerParent(parentForCreating);

        var parentCreated = parentRepository.findAll().getFirst();

        var parentId = parentCreated.getId();

        assertAll("Grouped assertions for created student",
                () -> assertEquals(parentForCreating.firstname(), parentCreated.getFirstname()),
                () -> assertEquals(parentForCreating.lastname(), parentCreated.getLastname()),
                () -> assertEquals(parentForCreating.patronymic(), parentCreated.getPatronymic()),
                () -> assertEquals(parentForCreating.email(), parentCreated.getEmail()));

        var parentForUpdating = getDefaultUserUpdateDTO();

        myUserDetailsService.updateUserByEmail(parentForCreating.email(), parentForUpdating);

        var parentUpdated = parentRepository.findById(parentId).get();

        assertAll("Grouped assertions for updated parent",
                () -> assertEquals(parentForUpdating.firstname(), parentUpdated.getFirstname()),
                () -> assertEquals(parentForUpdating.lastname(), parentUpdated.getLastname()),
                () -> assertEquals(parentForUpdating.patronymic(), parentUpdated.getPatronymic()));
    }

    @Test
    public void updateParentByNotExistedEmail() {
        var parentForCreating = getDefaultRegisterUserDTOByEmail("test@mail.ru");
        registrationService.registerParent(parentForCreating);

        var parentCreated = parentRepository.findAll().getFirst();

        assertAll("Grouped assertions for created parent",
                () -> assertEquals(parentForCreating.firstname(), parentCreated.getFirstname()),
                () -> assertEquals(parentForCreating.lastname(), parentCreated.getLastname()),
                () -> assertEquals(parentForCreating.patronymic(), parentCreated.getPatronymic()),
                () -> assertEquals(parentForCreating.email(), parentCreated.getEmail()));

        var parentForUpdating = getDefaultUserUpdateDTO();

        assertThrows(UserNotFoundException.class, () -> myUserDetailsService.updateUserByEmail("not_existed@mail.ru", parentForUpdating));
    }

    @Test
    public void deleteParentByEmail() {
        var parentForCreating = getDefaultRegisterUserDTOByEmail("test@mail.ru");
        registrationService.registerParent(parentForCreating);

        var parentCreated = parentRepository.findAll().getFirst();

        assertAll("Grouped assertions for created parent",
                () -> assertEquals(1, appUserRepository.count()),
                () -> assertEquals(1, parentRepository.count()),
                () -> assertEquals(parentForCreating.firstname(), parentCreated.getFirstname()),
                () -> assertEquals(parentForCreating.lastname(), parentCreated.getLastname()),
                () -> assertEquals(parentForCreating.patronymic(), parentCreated.getPatronymic()),
                () -> assertEquals(parentForCreating.email(), parentCreated.getEmail()));

        myUserDetailsService.deleteUserByEmail(parentForCreating.email());

        assertAll("Grouped assertions for deleted parent",
                () -> assertEquals(0, appUserRepository.count()),
                () -> assertEquals(0, parentRepository.count()),
                () -> assertThrows(UserNotFoundException.class, () -> myUserDetailsService.loadUserByUsername(parentForCreating.email())));
    }

    @Test
    public void deleteTeacherByEmail() {
        var teacherForCreating = getDefaultRegisterUserDTOByEmail("test@mail.ru");
        registrationService.registerTeacher(teacherForCreating);

        var teacherCreated = teacherRepository.findAll().getFirst();

        assertAll("Grouped assertions for created teacher",
                () -> assertEquals(teacherCreated.getFirstname(), teacherForCreating.firstname()),
                () -> assertEquals(teacherCreated.getLastname(), teacherForCreating.lastname()),
                () -> assertEquals(teacherCreated.getPatronymic(), teacherForCreating.patronymic()),
                () -> assertEquals(teacherCreated.getEmail(), teacherForCreating.email()));

        myUserDetailsService.deleteUserByEmail(teacherForCreating.email());

        assertAll("Grouped assertions for deleted teacher",
                () -> assertEquals(0, appUserRepository.count()),
                () -> assertEquals(0, teacherRepository.count()),
                () -> assertThrows(UserNotFoundException.class, () -> myUserDetailsService.loadUserByUsername(teacherForCreating.email())));
    }

    @Test
    public void deleteStudentByEmail() {
        var studentForCreating = getDefaultRegisterUserDTOByEmail("test@mail.ru");
        registrationService.registerStudent(studentForCreating);

        var studentCreated = studentRepository.findAll().getFirst();

        assertAll("Grouped assertions for created student",
                () -> assertEquals(studentForCreating.firstname(), studentCreated.getFirstname()),
                () -> assertEquals(studentForCreating.lastname(), studentCreated.getLastname()),
                () -> assertEquals(studentForCreating.patronymic(), studentCreated.getPatronymic()),
                () -> assertEquals(studentForCreating.email(), studentCreated.getEmail()));

        myUserDetailsService.deleteUserByEmail(studentForCreating.email());

        assertAll("Grouped assertions for deleted student",
                () -> assertEquals(0, appUserRepository.count()),
                () -> assertEquals(0, studentRepository.count()),
                () -> assertThrows(UserNotFoundException.class, () -> myUserDetailsService.loadUserByUsername(studentForCreating.email())));
    }
}