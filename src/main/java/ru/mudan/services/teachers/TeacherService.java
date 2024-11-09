package ru.mudan.services.teachers;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import ru.mudan.domain.entity.users.Teacher;
import ru.mudan.domain.repositories.TeacherRepository;
import ru.mudan.dto.teacher.TeacherDTO;
import ru.mudan.services.auth.MyUserDetailsService;

@Service
@RequiredArgsConstructor
public class TeacherService {

    private final TeacherRepository teacherRepository;
    private final MyUserDetailsService myUserDetailsService;

    public TeacherDTO findTeacherByAuth(Authentication authentication) {
        var teacher = (Teacher) myUserDetailsService.loadUserByUsername(authentication.getName());

        return TeacherDTO
                .builder()
                .id(teacher.getId())
                .firstname(teacher.getFirstname())
                .lastname(teacher.getLastname())
                .patronymic(teacher.getPatronymic())
                .email(teacher.getEmail())
                .build();
    }

    public List<TeacherDTO> findAll() {
        var teachers = teacherRepository.findAll();

        return teachers.stream()
                .map(teacher -> TeacherDTO
                        .builder()
                        .id(teacher.getId())
                        .firstname(teacher.getFirstname())
                        .lastname(teacher.getLastname())
                        .patronymic(teacher.getPatronymic())
                        .email(teacher.getEmail())
                        .build())
                .toList();
    }
}
