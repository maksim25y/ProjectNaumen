package ru.mudan.services.auth;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import ru.mudan.domain.entity.users.AppUser;
import ru.mudan.domain.repositories.*;
import ru.mudan.dto.users.UserUpdateDTO;
import ru.mudan.exceptions.entity.not_found.UserNotFoundException;

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

    public void updateUserByEmail(String email, UserUpdateDTO userUpdateDTO) {
        var appUser = appUserRepository.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException(email));

        switch (appUser.getRoleName()) {
            case ROLE_ADMIN -> {
                updateAdmin(email, userUpdateDTO, appUser);
            }
            case ROLE_PARENT -> {
                updateParent(email, userUpdateDTO, appUser);
            }
            case ROLE_STUDENT -> {
                updateStudent(email, userUpdateDTO, appUser);
            }
            case ROLE_TEACHER -> {
                updateTeacher(email, userUpdateDTO, appUser);
            }
            default -> throw new UsernameNotFoundException(email);
        }
    }

    public void deleteUserByEmail(String email) {
        var appUser = appUserRepository.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException(email));

        switch (appUser.getRoleName()) {
            case ROLE_PARENT -> {
                parentRepository.deleteById(appUser.getUserId());
                appUserRepository.delete(appUser);
            }
            case ROLE_STUDENT -> {
                studentRepository.deleteById(appUser.getUserId());
                appUserRepository.delete(appUser);
            }
            case ROLE_TEACHER -> {
                teacherRepository.deleteById(appUser.getUserId());
                appUserRepository.delete(appUser);
            }
            default -> throw new UsernameNotFoundException(email);
        }
    }

    private void updateTeacher(String email, UserUpdateDTO userUpdateDTO, AppUser appUser) {
        var foundTeacher = teacherRepository.findById(appUser.getUserId())
                .orElseThrow(() -> new UsernameNotFoundException(email));
        foundTeacher.setFirstname(userUpdateDTO.firstname());
        foundTeacher.setLastname(userUpdateDTO.lastname());
        foundTeacher.setPatronymic(userUpdateDTO.patronymic());
        teacherRepository.save(foundTeacher);
    }

    private void updateStudent(String email, UserUpdateDTO userUpdateDTO, AppUser appUser) {
        var foundStudent = studentRepository.findById(appUser.getUserId())
                .orElseThrow(() -> new UsernameNotFoundException(email));
        foundStudent.setFirstname(userUpdateDTO.firstname());
        foundStudent.setLastname(userUpdateDTO.lastname());
        foundStudent.setPatronymic(userUpdateDTO.patronymic());
        studentRepository.save(foundStudent);
    }

    private void updateParent(String email, UserUpdateDTO userUpdateDTO, AppUser appUser) {
        var foundParent = parentRepository.findById(appUser.getUserId())
                .orElseThrow(() -> new UsernameNotFoundException(email));
        foundParent.setFirstname(userUpdateDTO.firstname());
        foundParent.setLastname(userUpdateDTO.lastname());
        foundParent.setPatronymic(userUpdateDTO.patronymic());
        parentRepository.save(foundParent);
    }

    private void updateAdmin(String email, UserUpdateDTO userUpdateDTO, AppUser appUser) {
        var foundAdmin = adminRepository.findById(appUser.getUserId())
                .orElseThrow(() -> new UsernameNotFoundException(email));
        foundAdmin.setFirstname(userUpdateDTO.firstname());
        foundAdmin.setLastname(userUpdateDTO.lastname());
        foundAdmin.setPatronymic(userUpdateDTO.patronymic());
        adminRepository.save(foundAdmin);
    }

}
