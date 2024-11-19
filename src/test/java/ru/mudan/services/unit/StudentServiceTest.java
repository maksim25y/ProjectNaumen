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
import ru.mudan.domain.entity.users.Student;
import ru.mudan.domain.entity.users.enums.Role;
import ru.mudan.services.auth.MyUserDetailsService;
import ru.mudan.services.students.StudentService;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@AutoConfigureMockMvc
@SpringBootTest(classes = ProjectNaumenApplication.class)
public class StudentServiceTest {

    @MockBean
    private MyUserDetailsService myUserDetailsService;
    @Autowired
    private StudentService studentService;

    @Test
    public void findParentByAuth_ShouldReturnParentDTO() {
        var student = new Student( "Иван", "Иванов", "Иванович", "test@example.com", "test");

        UserDetails userDetails = new org.springframework.security.core.userdetails.User(
                student.getEmail(),
                "password",
                List.of(new SimpleGrantedAuthority(Role.ROLE_STUDENT.toString()))
        );

        Authentication authentication = new UsernamePasswordAuthenticationToken(
                userDetails,
                null,
                userDetails.getAuthorities()
        );

        when(myUserDetailsService.loadUserByUsername(anyString())).thenReturn(student);

        var studentDTO = studentService.findStudentByAuth(authentication);

        assertAll("Grouped assertions for found parent from auth",
                () -> assertEquals(student.getId(), studentDTO.id()),
                () -> assertEquals(student.getFirstname(), studentDTO.firstname()),
                () -> assertEquals(student.getLastname(), studentDTO.lastname()),
                () -> assertEquals(student.getPatronymic(), studentDTO.patronymic()),
                () -> assertEquals(student.getEmail(), studentDTO.email()));
    }
}