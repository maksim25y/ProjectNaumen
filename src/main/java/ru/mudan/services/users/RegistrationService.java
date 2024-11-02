package ru.mudan.services.users;

import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import ru.mudan.domain.entity.users.*;
import ru.mudan.domain.entity.users.enums.Role;
import ru.mudan.domain.repositories.*;
import ru.mudan.dto.RegisterUserDTO;
import ru.mudan.exceptions.UserAlreadyExistsException;

@Service
@RequiredArgsConstructor
public class RegistrationService {

    private final AdminRepository adminRepository;
    private final ParentRepository parentRepository;
    private final StudentRepository studentRepository;
    private final TeacherRepository teacherRepository;
    private final AppUserRepository appUserRepository;
    private final PasswordEncoder passwordEncoder;

    public void registerAdmin(RegisterUserDTO registerUserDTO) {
        checkUserExists(registerUserDTO.email());

        var admin = new Admin(
                registerUserDTO.firstname(),
                registerUserDTO.lastname(),
                registerUserDTO.patronymic(),
                registerUserDTO.email(),
                encodePassword(registerUserDTO.password()));

        var savedAdmin = adminRepository.save(admin);

        var appUser = new AppUser(
                savedAdmin.getId(),
                Role.ROLE_ADMIN,
                savedAdmin.getEmail()
        );

        appUserRepository.save(appUser);
    }

    public void registerTeacher(RegisterUserDTO registerUserDTO) {
        checkUserExists(registerUserDTO.email());

        var teacher = new Teacher(
                registerUserDTO.firstname(),
                registerUserDTO.lastname(),
                registerUserDTO.patronymic(),
                registerUserDTO.email(),
                encodePassword(registerUserDTO.password()));

        var savedTeacher = teacherRepository.save(teacher);

        var appUser = new AppUser(
                savedTeacher.getId(),
                Role.ROLE_ADMIN,
                savedTeacher.getEmail()
        );

        appUserRepository.save(appUser);
    }

    public void registerParent(RegisterUserDTO registerUserDTO) {
        checkUserExists(registerUserDTO.email());

        var parent = new Parent(
                registerUserDTO.firstname(),
                registerUserDTO.lastname(),
                registerUserDTO.patronymic(),
                registerUserDTO.email(),
                encodePassword(registerUserDTO.password()));

        var savedParent = parentRepository.save(parent);

        var appUser = new AppUser(
                savedParent.getId(),
                Role.ROLE_ADMIN,
                savedParent.getEmail()
        );

        appUserRepository.save(appUser);
    }

    public void registerStudent(RegisterUserDTO registerUserDTO) {
        checkUserExists(registerUserDTO.email());

        var student = new Student(
                registerUserDTO.firstname(),
                registerUserDTO.lastname(),
                registerUserDTO.patronymic(),
                registerUserDTO.email(),
                encodePassword(registerUserDTO.password()));

        var savedStudent = studentRepository.save(student);

        var appUser = new AppUser(
                savedStudent.getId(),
                Role.ROLE_ADMIN,
                savedStudent.getEmail()
        );

        appUserRepository.save(appUser);
    }

    private void checkUserExists(String email) {
        var user = appUserRepository.findByEmail(email);
        if (user.isPresent()) {
            throw new UserAlreadyExistsException("User already exists");
        }
    }

    private String encodePassword(String password) {
        return passwordEncoder.encode(password);
    }
}
