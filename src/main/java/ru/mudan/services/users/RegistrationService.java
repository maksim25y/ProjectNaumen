package ru.mudan.services.users;

import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.mudan.domain.entity.users.*;
import ru.mudan.domain.entity.users.enums.Role;
import ru.mudan.domain.repositories.*;
import ru.mudan.dto.auth.RegisterUserDTO;
import ru.mudan.exceptions.entity.already_exists.UserAlreadyExistsException;

@Service
@RequiredArgsConstructor
@Transactional
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

        var appUser = getAppUserByRoleUserIdAndEmail(
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

        var appUser = getAppUserByRoleUserIdAndEmail(
                savedTeacher.getId(),
                Role.ROLE_TEACHER,
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

        if (registerUserDTO.studentsIds() != null) {
            registerUserDTO.studentsIds().forEach(studentId -> {
               studentRepository.findById(studentId).ifPresent(student -> {
                   student.setParent(parent);
               });
            });
        }

        var appUser = getAppUserByRoleUserIdAndEmail(
                savedParent.getId(),
                Role.ROLE_PARENT,
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

        var appUser = getAppUserByRoleUserIdAndEmail(
                savedStudent.getId(),
                Role.ROLE_STUDENT,
                savedStudent.getEmail()
        );

        appUserRepository.save(appUser);
    }

    private void checkUserExists(String email) {
        var user = appUserRepository.findByEmail(email);
        if (user.isPresent()) {
            throw new UserAlreadyExistsException(email);
        }
    }

    private AppUser getAppUserByRoleUserIdAndEmail(Long userId, Role role, String email) {
        return new AppUser(
                userId,
                role,
                email
        );
    }

    private String encodePassword(String password) {
        return passwordEncoder.encode(password);
    }
}
