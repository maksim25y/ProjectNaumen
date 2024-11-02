package ru.mudan.services.users;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.mudan.domain.entity.users.Admin;
import ru.mudan.domain.entity.users.AppUser;
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

    public void registerAdmin(RegisterUserDTO registerUserDTO) {
        checkUserExists(registerUserDTO.email());

        var admin = new Admin(
                registerUserDTO.firstname(),
                registerUserDTO.lastname(),
                registerUserDTO.patronymic(),
                registerUserDTO.email());

        var savedAdmin = adminRepository.save(admin);

        var appUser = new AppUser(
                savedAdmin.getId(),
                Role.ROLE_ADMIN,
                savedAdmin.getEmail()
        );

        appUserRepository.save(appUser);
    }

    private void checkUserExists(String email) {
        var user = appUserRepository.findByEmail(email);
        if (user.isPresent()) {
            throw new UserAlreadyExistsException("User already exists");
        }
    }
}
