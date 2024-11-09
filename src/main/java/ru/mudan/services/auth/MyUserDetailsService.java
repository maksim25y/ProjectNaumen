package ru.mudan.services.auth;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import ru.mudan.domain.repositories.*;

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
        //TODO  - создать спец исключение для общего юзера
        var appUser = appUserRepository.findByEmail(username)
                .orElseThrow(() -> new UsernameNotFoundException(username));

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
}
