package ru.mudan.services.unit;

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
import ru.mudan.domain.entity.users.Parent;
import ru.mudan.domain.entity.users.enums.Role;
import ru.mudan.services.auth.MyUserDetailsService;
import ru.mudan.services.parent.ParentService;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@AutoConfigureMockMvc
@SpringBootTest(classes = ProjectNaumenApplication.class)
public class ParentServiceTest {

    @MockBean
    private MyUserDetailsService myUserDetailsService;
    @Autowired
    private ParentService parentService;

    @Test
    public void findParentByAuth_ShouldReturnParentDTO() {
        var parent = new Parent("Иван", "Иванов", "Иванович", "test@example.com", "test");

        UserDetails userDetails = new org.springframework.security.core.userdetails.User(
                parent.getEmail(),
                "password",
                List.of(new SimpleGrantedAuthority(Role.ROLE_PARENT.toString()))
        );

        Authentication authentication = new UsernamePasswordAuthenticationToken(
                userDetails,
                null,
                userDetails.getAuthorities()
        );

        when(myUserDetailsService.loadUserByUsername(anyString())).thenReturn(parent);

        var parentDTO = parentService.findParentByAuth(authentication);

        assertAll("Grouped assertions for found parent from auth",
                () -> assertEquals(parent.getId(), parentDTO.id()),
                () -> assertEquals(parent.getFirstname(), parentDTO.firstname()),
                () -> assertEquals(parent.getLastname(), parentDTO.lastname()),
                () -> assertEquals(parent.getPatronymic(), parentDTO.patronymic()),
                () -> assertEquals(parent.getEmail(), parentDTO.email()));
    }
}
