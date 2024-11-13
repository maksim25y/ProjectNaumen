package ru.mudan.controllers;

import java.util.List;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import ru.mudan.exceptions.base.ApplicationForbiddenException;
import ru.mudan.exceptions.entity.not_found.SubjectNotFoundException;
import ru.mudan.services.auth.AuthService;
import ru.mudan.services.grades.GradesService;
import ru.mudan.services.homework.HomeworkService;
import ru.mudan.services.schedule.ScheduleService;
import ru.mudan.services.students.StudentService;
import ru.mudan.services.subjects.SubjectService;
import ru.mudan.services.teachers.TeacherService;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static ru.mudan.controllers.UtilConstants.*;

@WithMockUser(roles = "TEACHER")
public class TeacherControllerTest extends BaseControllerTest {
    @MockBean
    private TeacherService teacherService;
    @MockBean
    private SubjectService subjectService;
    @MockBean
    private HomeworkService homeworkService;
    @MockBean
    private ScheduleService scheduleService;
    @MockBean
    private GradesService gradesService;
    @MockBean
    private StudentService studentService;
    @MockBean
    private AuthService authService;
    @MockBean
    private MessageSource messageSource;

    @Test
    @SneakyThrows
    public void getPageAccountOfTeacherWithRoleParent() {
        when(teacherService.findTeacherByAuth(any())).thenReturn(getDefaultTeacherDTO());
        when(subjectService.getSubjectsForTeacher(any())).thenReturn(List.of(getDefaultSubjectDTO()));

        mockMvc.perform(MockMvcRequestBuilders.get(TEACHER_URL + "/account")
                        .accept(MediaType.TEXT_HTML).with(csrf()))
                .andExpect(status().is(HttpStatus.OK.value()))
                .andExpect(view().name("teacher/teacher-main-page"))
                .andExpect(model().attributeExists("teacher"))
                .andExpect(model().attributeExists("subjects"));
    }

    @Test
    @SneakyThrows
    public void getPageHwForTeacherWithRoleTeacher() {
        when(teacherService.findTeacherByAuth(any())).thenReturn(getDefaultTeacherDTO());
        when(homeworkService.findAllBySubject(any())).thenReturn(List.of(getDefaultHomeworkDTO()));

        mockMvc.perform(MockMvcRequestBuilders.get(TEACHER_URL + "/hw/1")
                        .accept(MediaType.TEXT_HTML).with(csrf()))
                .andExpect(status().is(HttpStatus.OK.value()))
                .andExpect(view().name("teacher/homework/homeworks-show"))
                .andExpect(model().attributeExists("homeworks"));
    }

    @Test
    @SneakyThrows
    public void getPageHwForTeacherWithRoleTeacherAndSubjectDoesNotExists() {
        doThrow(SubjectNotFoundException.class).when(authService).teacherContainSubject(any(), any());
        when(messageSource.getMessage(any(), any(), any())).thenReturn("Предмет с id=1 не найден");

        mockMvc.perform(MockMvcRequestBuilders.get(TEACHER_URL + "/hw/1")
                        .accept(MediaType.TEXT_HTML).with(csrf()))
                .andExpect(status().is(HttpStatus.OK.value()))
                .andExpect(view().name("error/error-app"));
    }

    @Test
    @SneakyThrows
    public void getPageHwForTeacherWithRoleTeacherAndSubjectHasNotSubject() {
        doThrow(ApplicationForbiddenException.class).when(authService).teacherContainSubject(any(), any());

        mockMvc.perform(MockMvcRequestBuilders.get(TEACHER_URL + "/hw/1")
                        .accept(MediaType.TEXT_HTML).with(csrf()))
                .andExpect(status().is(HttpStatus.OK.value()))
                .andExpect(view().name("error/403"));
    }

    @Test
    @SneakyThrows
    public void getPageScheduleForTeacherWithRoleTeacher() {
        when(scheduleService.findAllBySubjectId(any())).thenReturn(List.of(getDefaultScheduleDTO()));

        mockMvc.perform(MockMvcRequestBuilders.get(TEACHER_URL + "/schedule/1")
                        .accept(MediaType.TEXT_HTML).with(csrf()))
                .andExpect(status().is(HttpStatus.OK.value()))
                .andExpect(view().name("teacher/schedule/schedule-teacher-index"))
                .andExpect(model().attributeExists("schedules"));
    }

    @Test
    @SneakyThrows
    public void getPageScheduleForTeacherWithRoleTeacherAndSubjectDoesNotExists() {
        doThrow(ApplicationForbiddenException.class).when(authService).teacherContainSubject(any(), any());

        mockMvc.perform(MockMvcRequestBuilders.get(TEACHER_URL + "/schedule/1")
                        .accept(MediaType.TEXT_HTML).with(csrf()))
                .andExpect(status().is(HttpStatus.OK.value()))
                .andExpect(view().name("error/403"));
    }

    @Test
    @SneakyThrows
    public void getPageHwForTeacherWithRoleTeacherAndTeacherHasNotSubject() {
        doThrow(ApplicationForbiddenException.class).when(authService).teacherContainSubject(any(), any());

        mockMvc.perform(MockMvcRequestBuilders.get(TEACHER_URL + "/schedule/1")
                        .accept(MediaType.TEXT_HTML).with(csrf()))
                .andExpect(status().is(HttpStatus.OK.value()))
                .andExpect(view().name("error/403"));
    }

    @Test
    @SneakyThrows
    public void getPageGradesForTeacherWithRoleTeacherAndSubjectExists() {
        when(subjectService.findById(any())).thenReturn(getDefaultSubjectDTO());
        when(gradesService.findAllBySubjectId(any())).thenReturn(List.of(getDefaultGradeDTOResponse()));

        mockMvc.perform(MockMvcRequestBuilders.get(TEACHER_URL + "/grades/subject/1")
                        .accept(MediaType.TEXT_HTML).with(csrf()))
                .andExpect(status().is(HttpStatus.OK.value()))
                .andExpect(view().name("teacher/grades/grades-index"))
                .andExpect(model().attributeExists("grades"));
    }

    @Test
    @SneakyThrows
    public void getPageGradesForTeacherWithRoleTeacherAndSubjectDoesNotExists() {
        doThrow(SubjectNotFoundException.class).when(authService).teacherContainSubject(any(), any());

        mockMvc.perform(MockMvcRequestBuilders.get(TEACHER_URL + "/grades/subject/1")
                        .accept(MediaType.TEXT_HTML).with(csrf()))
                .andExpect(status().is(HttpStatus.OK.value()))
                .andExpect(view().name("error/error-app"));
    }

    @Test
    @SneakyThrows
    public void getPageGradesForTeacherWithRoleTeacherAndTeacherHasNotSubject() {
        doThrow(ApplicationForbiddenException.class).when(authService).teacherContainSubject(any(), any());

        mockMvc.perform(MockMvcRequestBuilders.get(TEACHER_URL + "/grades/subject/1")
                        .accept(MediaType.TEXT_HTML).with(csrf()))
                .andExpect(status().is(HttpStatus.OK.value()))
                .andExpect(view().name("error/403"));
    }
}
