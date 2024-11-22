package ru.mudan.services.auth;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import ru.mudan.domain.entity.users.AppUser;
import ru.mudan.domain.repositories.*;
import ru.mudan.dto.users.UserUpdateDTO;
import ru.mudan.exceptions.entity.not_found.UserNotFoundException;

@Slf4j
@Service
@RequiredArgsConstructor
public class MyUserDetailsService implements UserDetailsService {

    private final AdminRepository adminRepository;
    private final ParentRepository parentRepository;
    private final StudentRepository studentRepository;
    private final TeacherRepository teacherRepository;
    private final AppUserRepository appUserRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        var appUser = appUserRepository.findByEmail(username)
                .orElseThrow(() -> new UserNotFoundException(username));

        switch (appUser.getRoleName()) {
            case ROLE_ADMIN -> {
                return adminRepository.findById(appUser.getUserId())
                        .orElseThrow(() -> new UsernameNotFoundException(username));
            }
            case ROLE_PARENT -> {
                return parentRepository.findById(appUser.getUserId())
                        .orElseThrow(() -> new UsernameNotFoundException(username));
            }
            case ROLE_STUDENT -> {
                return studentRepository.findById(appUser.getUserId())
                        .orElseThrow(() -> new UsernameNotFoundException(username));
            }
            case ROLE_TEACHER -> {
                return teacherRepository.findById(appUser.getUserId())
                        .orElseThrow(() -> new UsernameNotFoundException(username));
            }
            default -> throw new UsernameNotFoundException(username);
        }
    }

    /**
     * Метод для обновления данных пользователя по email
     *
     * @param userUpdateDTO - данные для обновления
     **/
    public void updateUser(UserUpdateDTO userUpdateDTO) {
        var appUser = appUserRepository.findByEmail(userUpdateDTO.email())
                .orElseThrow(() -> new UserNotFoundException(userUpdateDTO.email()));

        switch (appUser.getRoleName()) {
            case ROLE_ADMIN -> {
                updateAdmin(userUpdateDTO.email(), userUpdateDTO, appUser);
            }
            case ROLE_PARENT -> {
                updateParent(userUpdateDTO.email(), userUpdateDTO, appUser);
            }
            case ROLE_STUDENT -> {
                updateStudent(userUpdateDTO.email(), userUpdateDTO, appUser);
            }
            case ROLE_TEACHER -> {
                updateTeacher(userUpdateDTO.email(), userUpdateDTO, appUser);
            }
            default -> throw new UsernameNotFoundException(userUpdateDTO.email());
        }
    }

    /**
     * Метод для удаления данных пользователя по email
     *
     * @param email - email пользователя
     **/
    public void deleteUserByEmail(String email) {
        var appUser = appUserRepository.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException(email));

        switch (appUser.getRoleName()) {
            case ROLE_PARENT -> {
                log.info("Started deleting parent with email {}", email);
                parentRepository.deleteById(appUser.getUserId());
                appUserRepository.delete(appUser);
                log.info("Parent with email {} deleted successfully", email);
            }
            case ROLE_STUDENT -> {
                log.info("Started deleting student with email {}", email);
                studentRepository.deleteById(appUser.getUserId());
                appUserRepository.delete(appUser);
                log.info("Student with email {} deleted successfully", email);
            }
            case ROLE_TEACHER -> {
                log.info("Started deleting teacher with email {}", email);
                teacherRepository.deleteById(appUser.getUserId());
                appUserRepository.delete(appUser);
                log.info("Teacher with email {} deleted successfully", email);
            }
            default -> throw new UsernameNotFoundException(email);
        }
    }

    /**
     * Метод для обновления данных учителя по email
     *
     * @param email - email учителя
     **/
    private void updateTeacher(String email, UserUpdateDTO userUpdateDTO, AppUser appUser) {
        log.info("Started updating teacher with email {}", email);
        var foundTeacher = teacherRepository.findById(appUser.getUserId())
                .orElseThrow(() -> new UsernameNotFoundException(email));
        foundTeacher.setFirstname(userUpdateDTO.firstname());
        foundTeacher.setLastname(userUpdateDTO.lastname());
        foundTeacher.setPatronymic(userUpdateDTO.patronymic());
        teacherRepository.save(foundTeacher);
        log.info("Teacher with email {} updated successfully", email);
    }

    /**
     * Метод для обновления данных ученика по email
     *
     * @param email - email ученика
     **/
    private void updateStudent(String email, UserUpdateDTO userUpdateDTO, AppUser appUser) {
        log.info("Started updating student with email {}", email);
        var foundStudent = studentRepository.findById(appUser.getUserId())
                .orElseThrow(() -> new UsernameNotFoundException(email));
        foundStudent.setFirstname(userUpdateDTO.firstname());
        foundStudent.setLastname(userUpdateDTO.lastname());
        foundStudent.setPatronymic(userUpdateDTO.patronymic());
        studentRepository.save(foundStudent);
        log.info("Student with email {} updated successfully", email);
    }

    /**
     * Метод для обновления данных родителя по email
     *
     * @param email - email родителя
     **/
    private void updateParent(String email, UserUpdateDTO userUpdateDTO, AppUser appUser) {
        log.info("Started updating parent with email {}", email);
        var foundParent = parentRepository.findById(appUser.getUserId())
                .orElseThrow(() -> new UsernameNotFoundException(email));
        foundParent.setFirstname(userUpdateDTO.firstname());
        foundParent.setLastname(userUpdateDTO.lastname());
        foundParent.setPatronymic(userUpdateDTO.patronymic());
        parentRepository.save(foundParent);
        log.info("Parent with email {} updated successfully", email);
    }

    /**
     * Метод для обновления данных администратора по email
     *
     * @param email - email администратора
     **/
    private void updateAdmin(String email, UserUpdateDTO userUpdateDTO, AppUser appUser) {
        log.info("Started updating admin with email {}", email);
        var foundAdmin = adminRepository.findById(appUser.getUserId())
                .orElseThrow(() -> new UsernameNotFoundException(email));
        foundAdmin.setFirstname(userUpdateDTO.firstname());
        foundAdmin.setLastname(userUpdateDTO.lastname());
        foundAdmin.setPatronymic(userUpdateDTO.patronymic());
        adminRepository.save(foundAdmin);
        log.info("Admin with email {} updated successfully", email);
    }

}
