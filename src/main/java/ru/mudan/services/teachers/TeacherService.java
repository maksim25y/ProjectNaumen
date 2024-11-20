package ru.mudan.services.teachers;

import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import ru.mudan.domain.entity.users.Teacher;
import ru.mudan.domain.repositories.TeacherRepository;
import ru.mudan.dto.teacher.TeacherDTO;
import ru.mudan.exceptions.entity.not_found.TeacherNotFoundException;
import ru.mudan.services.auth.MyUserDetailsService;

/**
 * Класс с описанием бизнес-логики
 * для работы с сущностью Teacher
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class TeacherService {

    private final TeacherRepository teacherRepository;
    private final MyUserDetailsService myUserDetailsService;

    /**
     * Метод для получения текущего учителя по аутентификации
     *
     * @param authentication - аутентификация текущего пользователя
     */
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

    /**
     * Метод для получения списка всех учителей
     */
    public List<TeacherDTO> findAll() {
        log.info("Started getting all teachers");
        var teachers = teacherRepository.findAll();
        log.info("Finished getting all teachers");

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

    /**
     * Метод для получения учителя по id
     *
     * @param id - id учителя
     */
    public TeacherDTO findTeacherById(Long id) {
        var foundTeacher = teacherRepository.findById(id)
                .orElseThrow(() -> new TeacherNotFoundException(id));

        return TeacherDTO
                .builder()
                .id(foundTeacher.getId())
                .firstname(foundTeacher.getFirstname())
                .lastname(foundTeacher.getLastname())
                .patronymic(foundTeacher.getPatronymic())
                .email(foundTeacher.getEmail())
                .build();
    }
}
