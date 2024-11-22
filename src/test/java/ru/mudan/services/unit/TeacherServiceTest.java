package ru.mudan.services.unit;

import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import ru.mudan.ProjectNaumenApplication;
import ru.mudan.domain.entity.users.Teacher;
import ru.mudan.util.enums.Role;
import ru.mudan.services.auth.MyUserDetailsService;
import ru.mudan.services.teachers.TeacherService;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@AutoConfigureMockMvc
@SpringBootTest(classes = ProjectNaumenApplication.class)
public class TeacherServiceTest {

    @MockBean
    private MyUserDetailsService myUserDetailsService;
    @Autowired
    private TeacherService teacherService;

    @Test
    public void findParentByAuth_ShouldReturnParentDTO() {
        var teacher = new Teacher("Иван", "Иванов", "Иванович", "test@example.com", "test");

        UserDetails userDetails = new org.springframework.security.core.userdetails.User(
                teacher.getEmail(),
                "password",
                List.of(new SimpleGrantedAuthority(Role.ROLE_TEACHER.toString()))
        );

        Authentication authentication = new UsernamePasswordAuthenticationToken(
                userDetails,
                null,
                userDetails.getAuthorities()
        );

        when(myUserDetailsService.loadUserByUsername(anyString())).thenReturn(teacher);

        var teacherDTO = teacherService.findTeacherByAuth(authentication);

        assertAll("Grouped assertions for found parent from auth",
                () -> assertEquals(teacher.getId(), teacherDTO.id()),
                () -> assertEquals(teacher.getFirstname(), teacherDTO.firstname()),
                () -> assertEquals(teacher.getLastname(), teacherDTO.lastname()),
                () -> assertEquals(teacher.getPatronymic(), teacherDTO.patronymic()),
                () -> assertEquals(teacher.getEmail(), teacherDTO.email()));
    }
}