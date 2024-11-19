package ru.mudan.services.integration;


import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;
import ru.mudan.domain.repositories.*;
import ru.mudan.dto.auth.RegisterUserDTO;
import ru.mudan.exceptions.entity.already_exists.UserAlreadyExistsException;
import ru.mudan.services.users.RegistrationService;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static ru.mudan.UtilConstants.*;

@Rollback
@Transactional
public class RegistrationServiceIT extends IntegrationTest {

    @Autowired
    private RegistrationService registrationService;
    @Autowired
    private AppUserRepository appUserRepository;
    @Autowired
    private TeacherRepository teacherRepository;
    @Autowired
    private AdminRepository adminRepository;
    @Autowired
    private ParentRepository parentRepository;
    @Autowired
    private StudentRepository studentRepository;

    @Test
    public void registerNewTeacherTest() {
        var payload = getDefaultRegisterUserDTO();

        registrationService.registerTeacher(payload);

        assertAll("Grouped assertion for registered teacher",
                () -> assertTrue(appUserRepository.findByEmail(payload.email()).isPresent()),
                () -> assertEquals(1, teacherRepository.count()));
    }

    @Test
    public void registerNewAdminTest() {
        var payload = getDefaultRegisterUserDTO();

        registrationService.registerAdmin(payload);

        assertAll("Grouped assertion for registered admin",
                () -> assertTrue(appUserRepository.findByEmail(payload.email()).isPresent()),
                () -> assertEquals(1, adminRepository.count()));
    }

    @Test
    public void registerNewParentTest() {
        var payload = getDefaultRegisterUserDTO();

        registrationService.registerParent(payload);

        assertAll("Grouped assertion for registered parent",
                () -> assertTrue(appUserRepository.findByEmail(payload.email()).isPresent()),
                () -> assertEquals(1, parentRepository.count()));
    }

    @Test
    public void registerNewStudentTest() {
        var payload = getDefaultRegisterUserDTO();

        registrationService.registerStudent(payload);

        assertAll("Grouped assertion for registered student",
                () -> assertTrue(appUserRepository.findByEmail(payload.email()).isPresent()),
                () -> assertEquals(1, studentRepository.count()));
    }

    @Test
    public void registerNewTeacherUserAlreadyExistsTest() {
        var payload = getDefaultRegisterUserDTO();

        registrationService.registerTeacher(payload);

        assertAll("Grouped assertion for registered teacher",
                () -> assertTrue(appUserRepository.findByEmail(payload.email()).isPresent()),
                () -> assertEquals(1, teacherRepository.count()),
                () -> assertThrows(UserAlreadyExistsException.class, () -> registrationService.registerTeacher(payload)));
    }

    @Test
    public void registerNewAdminUserAlreadyExistsTest() {
        var payload = getDefaultRegisterUserDTO();

        registrationService.registerAdmin(payload);

        assertAll("Grouped assertion for registered admin",
                () -> assertTrue(appUserRepository.findByEmail(payload.email()).isPresent()),
                () -> assertEquals(1, adminRepository.count()),
                () -> assertThrows(UserAlreadyExistsException.class, () -> registrationService.registerAdmin(payload)));
    }

    @Test
    public void registerNewParentUserAlreadyExistsTest() {
        var payload = getDefaultRegisterUserDTO();

        registrationService.registerParent(payload);

        assertAll("Grouped assertion for registered parent",
                () -> assertTrue(appUserRepository.findByEmail(payload.email()).isPresent()),
                () -> assertEquals(1, parentRepository.count()),
                () -> assertThrows(UserAlreadyExistsException.class, () -> registrationService.registerParent(payload)));
    }

    @Test
    public void registerNewStudentUserAlreadyExistsTest() {
        var payload = getDefaultRegisterUserDTO();

        registrationService.registerStudent(payload);

        assertAll("Grouped assertion for registered student",
                () -> assertTrue(appUserRepository.findByEmail(payload.email()).isPresent()),
                () -> assertEquals(1, studentRepository.count()),
                () -> assertThrows(UserAlreadyExistsException.class, () -> registrationService.registerStudent(payload)));
    }

    @Test
    public void registerNewParentWithStudentsTest() {
        var payloadForCreatingStudent = getDefaultRegisterUserDTO();
        registrationService.registerStudent(getDefaultRegisterUserDTO());
        var idOfCreatedStudent = appUserRepository.findByEmail(payloadForCreatingStudent.email()).get().getUserId();

        var payload = RegisterUserDTO
                .builder()
                .firstname("Максим")
                .lastname("Максимов")
                .patronymic("Максимович")
                .password("test1234")
                .email("test1@mail.com")
                .studentsIds(List.of(idOfCreatedStudent))
                .build();

        registrationService.registerParent(payload);

        assertAll("Grouped assertion for registered parent",
                () -> assertTrue(appUserRepository.findByEmail(payload.email()).isPresent()),
                () -> assertEquals(1, parentRepository.count()),
                () -> assertNotNull(studentRepository.findById(idOfCreatedStudent).get().getParent()));
    }
}
