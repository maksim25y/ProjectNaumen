package ru.mudan.controllers;

import java.util.List;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import ru.mudan.services.parent.ParentService;
import ru.mudan.services.students.StudentService;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static ru.mudan.UtilConstants.*;

public class ParentControllerTest extends BaseControllerTest {

    @MockBean
    private ParentService parentService;
    @MockBean
    private StudentService studentService;

    @Test
    @SneakyThrows
    @WithMockUser(roles = "PARENT")
    public void getPageAccountOfParentWithRoleParent() {
        when(parentService.findParentByAuth(any())).thenReturn(getDefaultParent());
        when(studentService.getAllStudentsForParent(any())).thenReturn(List.of(getDefaultStudentDTO()));
        mockMvc.perform(MockMvcRequestBuilders.get(PARENT_URL + "/account")
                        .accept(MediaType.TEXT_HTML).with(csrf()))
                .andExpect(status().is(HttpStatus.OK.value()))
                .andExpect(view().name("parent/parent-main-page"))
                .andExpect(model().attributeExists("parent"))
                .andExpect(model().attributeExists("students"));
    }

    @Test
    @SneakyThrows
    @WithMockUser(roles = "ADMIN")
    public void getPageAccountOfParentWithRoleAdmin() {
        mockMvc.perform(MockMvcRequestBuilders.get(PARENT_URL + "/account")
                        .accept(MediaType.TEXT_HTML).with(csrf()))
                .andExpect(status().is(HttpStatus.FORBIDDEN.value()));
    }

    @Test
    @SneakyThrows
    @WithMockUser(roles = "STUDENT")
    public void getPageAccountOfParentWithRoleStudent() {
        mockMvc.perform(MockMvcRequestBuilders.get(PARENT_URL + "/account")
                        .accept(MediaType.TEXT_HTML).with(csrf()))
                .andExpect(status().is(HttpStatus.FORBIDDEN.value()));
    }

    @Test
    @SneakyThrows
    @WithMockUser(roles = "TEACHER")
    public void getPageAccountOfParentWithRoleTeacher() {
        mockMvc.perform(MockMvcRequestBuilders.get(PARENT_URL + "/account")
                        .accept(MediaType.TEXT_HTML).with(csrf()))
                .andExpect(status().is(HttpStatus.FORBIDDEN.value()));
    }
}
