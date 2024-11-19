package ru.mudan.controllers;

import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import ru.mudan.dto.student.StudentDTO;
import ru.mudan.services.students.StudentService;
import ru.mudan.services.subjects.SubjectService;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static ru.mudan.UtilConstants.*;

public class StudentControllerTest extends BaseControllerTest {

    @MockBean
    private StudentService studentService;
    @MockBean
    private SubjectService subjectService;

    @Test
    @SneakyThrows
    @WithMockUser(roles = "STUDENT")
    public void getPageAccountOfStudentWithRoleStudentWithClass() {
        when(studentService.findStudentByAuth(any())).thenReturn(getDefaultStudentDTO());
        when(subjectService.findAllSubjectsForClass(any())).thenReturn(List.of(getDefaultSubjectDTO()));
        mockMvc.perform(MockMvcRequestBuilders.get(STUDENT_URL + "/account")
                        .accept(MediaType.TEXT_HTML).with(csrf()))
                .andExpect(status().is(HttpStatus.OK.value()))
                .andExpect(view().name("student/student-main-page"))
                .andExpect(model().attributeExists("student"))
                .andExpect(model().attributeExists("subjects"));
    }

    @Test
    @SneakyThrows
    @WithMockUser(roles = "STUDENT")
    public void getPageAccountOfStudentWithRoleStudentWithNotClass() {
        var studentWithNotClass = StudentDTO
                .builder()
                .id(1L)
                .firstname("Иван")
                .lastname("Иванов")
                .patronymic("Иванович")
                .email("test@mail.ru")
                .build();

        when(studentService.findStudentByAuth(any())).thenReturn(studentWithNotClass);
        mockMvc.perform(MockMvcRequestBuilders.get(STUDENT_URL + "/account")
                        .accept(MediaType.TEXT_HTML).with(csrf()))
                .andExpect(status().is(HttpStatus.OK.value()))
                .andExpect(view().name("student/student-main-page"))
                .andExpect(model().attributeExists("student"))
                .andExpect(model().attributeDoesNotExist("subjects"));
    }

    @Test
    @SneakyThrows
    @WithMockUser(roles = "ADMIN")
    public void getPageAccountOfStudentWithRoleAdmin() {
        mockMvc.perform(MockMvcRequestBuilders.get(STUDENT_URL + "/account")
                        .accept(MediaType.TEXT_HTML).with(csrf()))
                .andExpect(status().is(HttpStatus.FORBIDDEN.value()));
    }

    @Test
    @SneakyThrows
    @WithMockUser(roles = "PARENT")
    public void getPageAccountOfStudentWithRoleParent() {
        mockMvc.perform(MockMvcRequestBuilders.get(STUDENT_URL + "/account")
                        .accept(MediaType.TEXT_HTML).with(csrf()))
                .andExpect(status().is(HttpStatus.FORBIDDEN.value()));
    }

    @Test
    @SneakyThrows
    @WithMockUser(roles = "TEACHER")
    public void getPageAccountOfStudentWithRoleTeacher() {
        mockMvc.perform(MockMvcRequestBuilders.get(STUDENT_URL + "/account")
                        .accept(MediaType.TEXT_HTML).with(csrf()))
                .andExpect(status().is(HttpStatus.FORBIDDEN.value()));
    }
}
