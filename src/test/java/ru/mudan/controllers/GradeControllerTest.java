package ru.mudan.controllers;

import java.time.LocalDate;
import java.util.List;
import java.util.function.Supplier;
import java.util.stream.Stream;

import lombok.SneakyThrows;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import ru.mudan.dto.grades.GradeDTO;
import ru.mudan.exceptions.base.ApplicationForbiddenException;
import ru.mudan.exceptions.entity.not_found.*;
import ru.mudan.services.auth.AuthService;
import ru.mudan.services.grades.GradesService;
import ru.mudan.services.students.StudentService;
import ru.mudan.services.subjects.SubjectService;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static ru.mudan.controllers.UtilConstants.*;

public class GradeControllerTest extends BaseControllerTest {

    private static final Supplier<Stream<Arguments>> invalidGradeDTOs = () -> Stream.of(
            Arguments.of(new GradeDTO(null, 1, LocalDate.now(), "Комментарий", 1L, 1L)),
            Arguments.of(new GradeDTO(null, 6, LocalDate.now(), "Комментарий", 1L, 1L)),
            Arguments.of(new GradeDTO(null, 4, null, "Комментарий", 1L, 1L))
            );

    public static Stream<Arguments> provideInvalidGradeDTOs() {
        return invalidGradeDTOs.get();
    }

    @MockBean
    private GradesService gradesService;
    @MockBean
    private AuthService authService;
    @MockBean
    private SubjectService subjectService;
    @MockBean
    private StudentService studentService;
    @MockBean
    private MessageSource messageSource;

    @Test
    @WithMockUser(roles = "ADMIN")
    @SneakyThrows
    public void getAllGradesForExistedStudentBySubjectForRoleAdmin() {
        when(subjectService.findById(any())).thenReturn(getDefaultSubjectDTO());
        when(gradesService.findAllGradesForStudentWithSubject(any(), any())).thenReturn(List.of(getDefaultGradeDTO()));
        when(studentService.findById(any())).thenReturn(getDefaultStudentDTO());

        mockMvc.perform(MockMvcRequestBuilders.get(GRADES_URL + "/all/1?subjectId=1")
                        .accept(MediaType.TEXT_HTML).with(csrf()))
                .andExpect(status().is(HttpStatus.OK.value()))
                .andExpect(model().attributeExists("student"))
                .andExpect(model().attributeExists("grades"))
                .andExpect(model().attributeExists("subject"));
    }

    @Test
    @WithMockUser(roles = "STUDENT")
    @SneakyThrows
    public void getAllGradesForExistedStudentBySubjectForRoleCurrentStudent() {
        when(subjectService.findById(any())).thenReturn(getDefaultSubjectDTO());
        when(gradesService.findAllGradesForStudentWithSubject(any(), any())).thenReturn(List.of(getDefaultGradeDTO()));
        when(studentService.findById(any())).thenReturn(getDefaultStudentDTO());

        mockMvc.perform(MockMvcRequestBuilders.get(GRADES_URL + "/all/1?subjectId=1")
                        .accept(MediaType.TEXT_HTML).with(csrf()))
                .andExpect(status().is(HttpStatus.OK.value()))
                .andExpect(model().attributeExists("student"))
                .andExpect(model().attributeExists("grades"))
                .andExpect(model().attributeExists("subject"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    @SneakyThrows
    public void getAllGradesForNotExistedStudentBySubjectForRoleAdmin() {
        when(studentService.findById(any())).thenThrow(StudentNotFoundException.class);
        when(messageSource.getMessage(any(), any(), any())).thenReturn("Ученик с id=1 не найден");

        mockMvc.perform(MockMvcRequestBuilders.get(GRADES_URL + "/all/1?subjectId=1")
                        .accept(MediaType.TEXT_HTML).with(csrf()))
                .andExpect(status().is(HttpStatus.OK.value()))
                .andExpect(view().name("error/error-app"))
                .andExpect(model().attributeExists("error"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    @SneakyThrows
    public void getAllGradesForNotExistedStudentWithNotExistedSubject() {
        when(subjectService.findById(any())).thenThrow(SubjectNotFoundException.class);
        when(messageSource.getMessage(any(), any(), any())).thenReturn("Предмет с id=1 не найден");


        mockMvc.perform(MockMvcRequestBuilders.get(GRADES_URL + "/all/1?subjectId=1")
                        .accept(MediaType.TEXT_HTML).with(csrf()))
                .andExpect(status().is(HttpStatus.OK.value()))
                .andExpect(view().name("error/error-app"))
                .andExpect(model().attributeExists("error"));
    }

    @Test
    @WithMockUser(roles = "TEACHER")
    @SneakyThrows
    public void getAllGradesForExistedStudentBySubjectForRoleTeacher() {
        doThrow(ApplicationForbiddenException.class).when(authService).hasRoleAdminOrStudentThatInClassThatContainsSubject(any(), any(), any());

        mockMvc.perform(MockMvcRequestBuilders.get(GRADES_URL + "/all/1?subjectId=1")
                        .accept(MediaType.TEXT_HTML).with(csrf()))
                .andExpect(status().is(HttpStatus.OK.value()))
                .andExpect(view().name("error/403"))
                .andExpect(model().attributeDoesNotExist("student"))
                .andExpect(model().attributeDoesNotExist("grades"))
                .andExpect(model().attributeDoesNotExist("subject"));
    }

    @Test
    @WithMockUser(roles = "STUDENT")
    @SneakyThrows
    public void getAllGradesForExistedStudentBySubjectForRoleNotCurrentStudent() {
        doThrow(ApplicationForbiddenException.class).when(authService).hasRoleAdminOrStudentThatInClassThatContainsSubject(any(), any(), any());

        mockMvc.perform(MockMvcRequestBuilders.get(GRADES_URL + "/all/1?subjectId=1")
                        .accept(MediaType.TEXT_HTML).with(csrf()))
                .andExpect(status().is(HttpStatus.OK.value()))
                .andExpect(view().name("error/403"))
                .andExpect(model().attributeDoesNotExist("student"))
                .andExpect(model().attributeDoesNotExist("grades"))
                .andExpect(model().attributeDoesNotExist("subject"));
    }

    @Test
    @WithMockUser(roles = "PARENT")
    @SneakyThrows
    public void getAllGradesForExistedStudentByExistedSubjectForRoleParent() {
        doThrow(ApplicationForbiddenException.class).when(authService).hasRoleAdminOrStudentThatInClassThatContainsSubject(any(), any(), any());

        mockMvc.perform(MockMvcRequestBuilders.get(GRADES_URL + "/all/1?subjectId=1")
                        .accept(MediaType.TEXT_HTML).with(csrf()))
                .andExpect(status().is(HttpStatus.OK.value()))
                .andExpect(view().name("error/403"))
                .andExpect(model().attributeDoesNotExist("student"))
                .andExpect(model().attributeDoesNotExist("grades"))
                .andExpect(model().attributeDoesNotExist("subject"));
    }

    @Test
    @WithMockUser(roles = "PARENT")
    @SneakyThrows
    public void getAllGradesForExistedStudentForRoleParent() {
        doThrow(ApplicationForbiddenException.class).when(authService).hasRoleAdminOrStudentThatInClassThatContainsSubject(any(), any(), any());

        mockMvc.perform(MockMvcRequestBuilders.get(GRADES_URL + "/all/1")
                        .accept(MediaType.TEXT_HTML).with(csrf()))
                .andExpect(status().is(HttpStatus.OK.value()))
                .andExpect(view().name("error/403"))
                .andExpect(model().attributeDoesNotExist("student"))
                .andExpect(model().attributeDoesNotExist("grades"))
                .andExpect(model().attributeDoesNotExist("subject"));
    }

    @Test
    @WithMockUser(roles = "TEACHER")
    @SneakyThrows
    public void getAllGradesForExistedStudentForRoleTeacher() {
        doThrow(ApplicationForbiddenException.class).when(authService).hasRoleAdminOrStudentThatInClassThatContainsSubject(any(), any(), any());

        mockMvc.perform(MockMvcRequestBuilders.get(GRADES_URL + "/all/1")
                        .accept(MediaType.TEXT_HTML).with(csrf()))
                .andExpect(status().is(HttpStatus.OK.value()))
                .andExpect(view().name("error/403"))
                .andExpect(model().attributeDoesNotExist("student"))
                .andExpect(model().attributeDoesNotExist("grades"))
                .andExpect(model().attributeDoesNotExist("subject"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    @SneakyThrows
    public void getAllGradesForExistedStudentForRoleAdmin() {
        when(subjectService.findById(any())).thenReturn(getDefaultSubjectDTO());
        when(gradesService.findAllGradesForStudent(any())).thenReturn(List.of(getDefaultGradeDTO()));
        when(studentService.findById(any())).thenReturn(getDefaultStudentDTO());

        mockMvc.perform(MockMvcRequestBuilders.get(GRADES_URL + "/all/1")
                        .accept(MediaType.TEXT_HTML).with(csrf()))
                .andExpect(status().is(HttpStatus.OK.value()))
                .andExpect(model().attributeExists("student"))
                .andExpect(model().attributeExists("grades"));
    }

    @Test
    @WithMockUser(roles = "STUDENT")
    @SneakyThrows
    public void getAllGradesForExistedStudentForRoleCurrentStudent() {
        when(gradesService.findAllGradesForStudent(any())).thenReturn(List.of(getDefaultGradeDTO()));
        when(studentService.findById(any())).thenReturn(getDefaultStudentDTO());

        mockMvc.perform(MockMvcRequestBuilders.get(GRADES_URL + "/all/1")
                        .accept(MediaType.TEXT_HTML).with(csrf()))
                .andExpect(status().is(HttpStatus.OK.value()))
                .andExpect(model().attributeExists("student"))
                .andExpect(model().attributeExists("grades"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    @SneakyThrows
    public void getAllGradesForNotExistedStudentForRoleAdmin() {
        when(studentService.findById(any())).thenThrow(StudentNotFoundException.class);
        when(messageSource.getMessage(any(), any(), any())).thenReturn("Ученик с id=1 не найден");

        mockMvc.perform(MockMvcRequestBuilders.get(GRADES_URL + "/all/1")
                        .accept(MediaType.TEXT_HTML).with(csrf()))
                .andExpect(status().is(HttpStatus.OK.value()))
                .andExpect(view().name("error/error-app"))
                .andExpect(model().attributeExists("error"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    @SneakyThrows
    public void getExistedGradeByIdForRoleAdmin() {
        when(gradesService.findById(any())).thenReturn(getDefaultGradeDTO());

        mockMvc.perform(MockMvcRequestBuilders.get(GRADES_URL + "/1")
                        .accept(MediaType.TEXT_HTML).with(csrf()))
                .andExpect(status().is(HttpStatus.OK.value()))
                .andExpect(model().attributeExists("grade"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    @SneakyThrows
    public void getNotExistedGradeByIdForRoleAdmin() {
        when(gradesService.findById(any())).thenThrow(GradeNotFoundException.class);
        when(messageSource.getMessage(any(), any(), any())).thenReturn("Оценка с id=1 не найдена");

        mockMvc.perform(MockMvcRequestBuilders.get(GRADES_URL + "/1")
                        .accept(MediaType.TEXT_HTML).with(csrf()))
                .andExpect(status().is(HttpStatus.OK.value()))
                .andExpect(view().name("error/error-app"))
                .andExpect(model().attributeExists("error"));
    }

    @Test
    @WithMockUser(roles = "TEACHER")
    @SneakyThrows
    public void getExistedGradeByIdForRoleTeacherHasGrade() {
        when(gradesService.findById(any())).thenReturn(getDefaultGradeDTO());

        mockMvc.perform(MockMvcRequestBuilders.get(GRADES_URL + "/1")
                        .accept(MediaType.TEXT_HTML).with(csrf()))
                .andExpect(status().is(HttpStatus.OK.value()))
                .andExpect(model().attributeExists("grade"));
    }

    @Test
    @WithMockUser(roles = "TEACHER")
    @SneakyThrows
    public void getNotExistedGradeByIdForRoleTeacherHasNotGrade() {
        doThrow(ApplicationForbiddenException.class).when(authService).teacherHasGradeOrRoleIsAdmin(any(), any());

        mockMvc.perform(MockMvcRequestBuilders.get(GRADES_URL + "/1")
                        .accept(MediaType.TEXT_HTML).with(csrf()))
                .andExpect(status().is(HttpStatus.OK.value()))
                .andExpect(view().name("error/403"));
    }

    @Test
    @WithMockUser(roles = "PARENT")
    @SneakyThrows
    public void getNotExistedGradeByIdForRoleParent() {
        doThrow(ApplicationForbiddenException.class).when(authService).teacherHasGradeOrRoleIsAdmin(any(), any());

        mockMvc.perform(MockMvcRequestBuilders.get(GRADES_URL + "/1")
                        .accept(MediaType.TEXT_HTML).with(csrf()))
                .andExpect(status().is(HttpStatus.OK.value()))
                .andExpect(view().name("error/403"));
    }

    @Test
    @WithMockUser(roles = "STUDENT")
    @SneakyThrows
    public void getNotExistedGradeByIdForRoleStudent() {
        doThrow(ApplicationForbiddenException.class).when(authService).teacherHasGradeOrRoleIsAdmin(any(), any());

        mockMvc.perform(MockMvcRequestBuilders.get(GRADES_URL + "/1")
                        .accept(MediaType.TEXT_HTML).with(csrf()))
                .andExpect(status().is(HttpStatus.OK.value()))
                .andExpect(view().name("error/403"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    @SneakyThrows
    public void getPageForEditingGradeForRoleAdmin() {
        when(gradesService.findById(any())).thenReturn(getDefaultGradeDTO());

        mockMvc.perform(MockMvcRequestBuilders.get(GRADES_URL + "/1/edit")
                        .accept(MediaType.TEXT_HTML).with(csrf()))
                .andExpect(status().is(HttpStatus.OK.value()))
                .andExpect(view().name("grades/grades-edit"))
                .andExpect(model().attributeExists("grade"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    @SneakyThrows
    public void getPageForEditingNotExistedGradeForRoleAdmin() {
        when(gradesService.findById(any())).thenThrow(GradeNotFoundException.class);
        when(messageSource.getMessage(any(), any(), any())).thenReturn("Оценка с id=1 не найдена");

        mockMvc.perform(MockMvcRequestBuilders.get(GRADES_URL + "/1/edit")
                        .accept(MediaType.TEXT_HTML).with(csrf()))
                .andExpect(status().is(HttpStatus.OK.value()))
                .andExpect(view().name("error/error-app"))
                .andExpect(model().attributeExists("error"));
    }

    @Test
    @WithMockUser(roles = "TEACHER")
    @SneakyThrows
    public void getPageForEditingGradeForRoleTeacherHasGrade() {
        when(gradesService.findById(any())).thenReturn(getDefaultGradeDTO());

        mockMvc.perform(MockMvcRequestBuilders.get(GRADES_URL + "/1/edit")
                        .accept(MediaType.TEXT_HTML).with(csrf()))
                .andExpect(status().is(HttpStatus.OK.value()))
                .andExpect(view().name("grades/grades-edit"))
                .andExpect(model().attributeExists("grade"));
    }

    @Test
    @WithMockUser(roles = "TEACHER")
    @SneakyThrows
    public void getPageForEditingGradeForRoleTeacherHasNotGrade() {
        doThrow(ApplicationForbiddenException.class).when(authService).teacherHasGradeOrRoleIsAdmin(any(), any());

        mockMvc.perform(MockMvcRequestBuilders.get(GRADES_URL + "/1/edit")
                        .accept(MediaType.TEXT_HTML).with(csrf()))
                .andExpect(status().is(HttpStatus.OK.value()))
                .andExpect(view().name("error/403"));
    }

    @Test
    @WithMockUser(roles = "PARENT")
    @SneakyThrows
    public void getPageForEditingGradeForRoleParent() {
        doThrow(ApplicationForbiddenException.class).when(authService).teacherHasGradeOrRoleIsAdmin(any(), any());

        mockMvc.perform(MockMvcRequestBuilders.get(GRADES_URL + "/1/edit")
                        .accept(MediaType.TEXT_HTML).with(csrf()))
                .andExpect(status().is(HttpStatus.OK.value()))
                .andExpect(view().name("error/403"));
    }

    @Test
    @WithMockUser(roles = "STUDENT")
    @SneakyThrows
    public void getPageForEditingGradeForRoleStudent() {
        doThrow(ApplicationForbiddenException.class).when(authService).teacherHasGradeOrRoleIsAdmin(any(), any());

        mockMvc.perform(MockMvcRequestBuilders.get(GRADES_URL + "/1/edit")
                        .accept(MediaType.TEXT_HTML).with(csrf()))
                .andExpect(status().is(HttpStatus.OK.value()))
                .andExpect(view().name("error/403"));
    }

    @Test
    @SneakyThrows
    @WithMockUser(roles = "ADMIN")
    public void deleteExistedGradeForRoleAdmin() {
        mockMvc.perform(MockMvcRequestBuilders.delete(GRADES_URL + "/1")
                        .accept(MediaType.TEXT_HTML).with(csrf()))
                .andExpect(status().is(HttpStatus.FOUND.value()));
    }

    @Test
    @SneakyThrows
    @WithMockUser(roles = "ADMIN")
    public void deleteNotExistedGradeForRoleAdmin() {
        doThrow(GradeNotFoundException.class).when(authService).teacherHasGradeOrRoleIsAdmin(any(), any());
        when(messageSource.getMessage(any(), any(), any())).thenReturn("Оценка c id=1 не найдена");

        mockMvc.perform(MockMvcRequestBuilders.delete(GRADES_URL + "/1")
                        .accept(MediaType.TEXT_HTML).with(csrf()))
                .andExpect(status().is(HttpStatus.OK.value()))
                .andExpect(view().name("error/error-app"))
                .andExpect(model().attributeExists("error"));
    }

    @Test
    @SneakyThrows
    @WithMockUser(roles = "TEACHER")
    public void deleteExistedGradeForRoleTeacherThatHasGrade() {
        mockMvc.perform(MockMvcRequestBuilders.delete(GRADES_URL + "/1")
                        .accept(MediaType.TEXT_HTML).with(csrf()))
                .andExpect(status().is(HttpStatus.FOUND.value()));
    }

    @Test
    @SneakyThrows
    @WithMockUser(roles = "TEACHER")
    public void deleteNotExistedGradeForRoleTeacherThatHasNotGrade() {
        doThrow(ApplicationForbiddenException.class).when(authService).teacherHasGradeOrRoleIsAdmin(any(), any());

        mockMvc.perform(MockMvcRequestBuilders.delete(GRADES_URL + "/1")
                        .accept(MediaType.TEXT_HTML).with(csrf()))
                .andExpect(status().is(HttpStatus.OK.value()))
                .andExpect(view().name("error/403"));
    }

    @Test
    @WithMockUser(roles = "PARENT")
    @SneakyThrows
    public void deleteNotExistedGradeForRoleParent() {
        doThrow(ApplicationForbiddenException.class).when(authService).teacherHasGradeOrRoleIsAdmin(any(), any());

        mockMvc.perform(MockMvcRequestBuilders.delete(GRADES_URL + "/1")
                        .accept(MediaType.TEXT_HTML).with(csrf()))
                .andExpect(status().is(HttpStatus.OK.value()))
                .andExpect(view().name("error/403"));
    }

    @Test
    @WithMockUser(roles = "STUDENT")
    @SneakyThrows
    public void deleteNotExistedGradeForRoleStudent() {
        doThrow(ApplicationForbiddenException.class).when(authService).teacherHasGradeOrRoleIsAdmin(any(), any());

        mockMvc.perform(MockMvcRequestBuilders.delete(GRADES_URL + "/1")
                        .accept(MediaType.TEXT_HTML).with(csrf()))
                .andExpect(status().is(HttpStatus.OK.value()))
                .andExpect(view().name("error/403"));
    }

    @Test
    @SneakyThrows
    @WithMockUser(roles = "ADMIN")
    public void postCreateGradeValidForRoleAdmin() {
        var payload = getDefaultGradeDTO();

        mockMvc.perform(MockMvcRequestBuilders.post(GRADES_URL)
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .param("mark", String.valueOf(payload.mark()))
                        .param("dateOfMark", String.valueOf(payload.dateOfMark()))
                        .param("comment", payload.comment())
                        .param("studentId", String.valueOf(payload.studentId()))
                        .param("subjectId", String.valueOf(payload.subjectId()))
                        .with(csrf()))
                .andExpect(status().is(HttpStatus.FOUND.value()));
    }

    @Test
    @SneakyThrows
    @WithMockUser(roles = "TEACHER")
    public void postCreateGradeValidForRoleTeacherHasGrade() {
        var payload = getDefaultGradeDTO();

        mockMvc.perform(MockMvcRequestBuilders.post(GRADES_URL)
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .param("mark", String.valueOf(payload.mark()))
                        .param("dateOfMark", String.valueOf(payload.dateOfMark()))
                        .param("comment", payload.comment())
                        .param("studentId", String.valueOf(payload.studentId()))
                        .param("subjectId", String.valueOf(payload.subjectId()))
                        .with(csrf()))
                .andExpect(status().is(HttpStatus.FOUND.value()));
    }

    @Test
    @SneakyThrows
    @WithMockUser(roles = "TEACHER")
    public void postCreateGradeValidForRoleTeacherHasNotGrade() {
        var payload = getDefaultGradeDTO();
        doThrow(ApplicationForbiddenException.class).when(authService).teacherHasSubjectOrRoleIsAdmin(any(), any());

        mockMvc.perform(MockMvcRequestBuilders.post(GRADES_URL)
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .param("mark", String.valueOf(payload.mark()))
                        .param("dateOfMark", String.valueOf(payload.dateOfMark()))
                        .param("comment", payload.comment())
                        .param("studentId", String.valueOf(payload.studentId()))
                        .param("subjectId", String.valueOf(payload.subjectId()))
                        .with(csrf()))
                .andExpect(status().is(HttpStatus.OK.value()))
                .andExpect(view().name("error/403"));
    }

    @Test
    @SneakyThrows
    @WithMockUser(roles = "PARENT")
    public void postCreateGradeValidForRoleParent() {
        var payload = getDefaultGradeDTO();
        doThrow(ApplicationForbiddenException.class).when(authService).teacherHasSubjectOrRoleIsAdmin(any(), any());

        mockMvc.perform(MockMvcRequestBuilders.post(GRADES_URL)
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .param("mark", String.valueOf(payload.mark()))
                        .param("dateOfMark", String.valueOf(payload.dateOfMark()))
                        .param("comment", payload.comment())
                        .param("studentId", String.valueOf(payload.studentId()))
                        .param("subjectId", String.valueOf(payload.subjectId()))
                        .with(csrf()))
                .andExpect(status().is(HttpStatus.OK.value()))
                .andExpect(view().name("error/403"));
    }

    @Test
    @SneakyThrows
    @WithMockUser(roles = "STUDENT")
    public void postCreateGradeValidForRoleStudent() {
        var payload = getDefaultGradeDTO();
        doThrow(ApplicationForbiddenException.class).when(authService).teacherHasSubjectOrRoleIsAdmin(any(), any());

        mockMvc.perform(MockMvcRequestBuilders.post(GRADES_URL)
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .param("mark", String.valueOf(payload.mark()))
                        .param("dateOfMark", String.valueOf(payload.dateOfMark()))
                        .param("comment", payload.comment())
                        .param("studentId", String.valueOf(payload.studentId()))
                        .param("subjectId", String.valueOf(payload.subjectId()))
                        .with(csrf()))
                .andExpect(status().is(HttpStatus.OK.value()))
                .andExpect(view().name("error/403"));
    }

    @Test
    @SneakyThrows
    @WithMockUser(roles = "ADMIN")
    public void putUpdateGradeValidForRoleAdmin() {
        var payload = getDefaultGradeDTO();

        mockMvc.perform(MockMvcRequestBuilders.put(GRADES_URL+"/1")
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .param("mark", String.valueOf(payload.mark()))
                        .param("dateOfMark", String.valueOf(payload.dateOfMark()))
                        .param("comment", payload.comment())
                        .with(csrf()))
                .andExpect(status().is(HttpStatus.FOUND.value()));
    }

    @Test
    @SneakyThrows
    @WithMockUser(roles = "TEACHER")
    public void putUpdateGradeValidForRoleTeacherThatHasGrade() {
        var payload = getDefaultGradeDTO();

        mockMvc.perform(MockMvcRequestBuilders.put(GRADES_URL+"/1")
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .param("mark", String.valueOf(payload.mark()))
                        .param("dateOfMark", String.valueOf(payload.dateOfMark()))
                        .param("comment", payload.comment())
                        .with(csrf()))
                .andExpect(status().is(HttpStatus.FOUND.value()));
    }

    @Test
    @SneakyThrows
    @WithMockUser(roles = "PARENT")
    public void putUpdateGradeValidForRoleParent() {
        var payload = getDefaultGradeDTO();
        doThrow(ApplicationForbiddenException.class).when(authService).teacherHasGradeOrRoleIsAdmin(any(), any());

        mockMvc.perform(MockMvcRequestBuilders.put(GRADES_URL+"/1")
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .param("mark", String.valueOf(payload.mark()))
                        .param("dateOfMark", String.valueOf(payload.dateOfMark()))
                        .param("comment", payload.comment())
                        .with(csrf()))
                .andExpect(status().is(HttpStatus.OK.value()))
                .andExpect(view().name("error/403"));
    }

    @Test
    @SneakyThrows
    @WithMockUser(roles = "STUDENT")
    public void putUpdateGradeValidForRoleStudent() {
        var payload = getDefaultGradeDTO();
        doThrow(ApplicationForbiddenException.class).when(authService).teacherHasGradeOrRoleIsAdmin(any(), any());

        mockMvc.perform(MockMvcRequestBuilders.put(GRADES_URL+"/1")
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .param("mark", String.valueOf(payload.mark()))
                        .param("dateOfMark", String.valueOf(payload.dateOfMark()))
                        .param("comment", payload.comment())
                        .with(csrf()))
                .andExpect(status().is(HttpStatus.OK.value()))
                .andExpect(view().name("error/403"));
    }

    @Test
    @SneakyThrows
    @WithMockUser(roles = "ADMIN")
    public void putUpdateNotExistedGradeValidForRoleAdmin() {
        var payload = getDefaultGradeDTO();
        doThrow(GradeNotFoundException.class).when(gradesService).update(any(), any());
        when(messageSource.getMessage(any(), any(), any())).thenReturn("Оценка с id=1 не найдена");

        mockMvc.perform(MockMvcRequestBuilders.put(GRADES_URL+"/1")
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .param("mark", String.valueOf(payload.mark()))
                        .param("dateOfMark", String.valueOf(payload.dateOfMark()))
                        .param("comment", payload.comment())
                        .with(csrf()))
                .andExpect(status().is(HttpStatus.OK.value()))
                .andExpect(view().name("error/error-app"))
                .andExpect(model().attributeExists("error"));
    }

    @SneakyThrows
    @ParameterizedTest
    @WithMockUser(roles = "ADMIN")
    @DisplayName("Should return status 200 and errors")
    @MethodSource("provideInvalidGradeDTOs")
    public void postCreateGradeInvalidForRoleAdmin(GradeDTO gradeDTO) {
        checkPostCreateInvalidGrade(gradeDTO);
    }

    @SneakyThrows
    @ParameterizedTest
    @WithMockUser(roles = "ADMIN")
    @DisplayName("Should return status 200 and errors")
    @MethodSource("provideInvalidGradeDTOs")
    public void putUpdateGradeInvalidForRoleAdmin(GradeDTO gradeDTO) {
        checkPutUpdateInvalidGrade(gradeDTO);
    }

    @SneakyThrows
    @ParameterizedTest
    @WithMockUser(roles = "TEACHER")
    @DisplayName("Should return status 200 and errors")
    @MethodSource("provideInvalidGradeDTOs")
    public void postCreateGradeInvalidForTeacherHasSubject(GradeDTO gradeDTO) {
        checkPostCreateInvalidGrade(gradeDTO);
    }

    @SneakyThrows
    @ParameterizedTest
    @WithMockUser(roles = "TEACHER")
    @DisplayName("Should return status 200 and errors")
    @MethodSource("provideInvalidGradeDTOs")
    public void putUpdateGradeInvalidForTeacherHasGrade(GradeDTO gradeDTO) {
        checkPutUpdateInvalidGrade(gradeDTO);
    }

    @SneakyThrows
    private void checkPutUpdateInvalidGrade(GradeDTO gradeDTO) {
        mockMvc.perform(MockMvcRequestBuilders.put(GRADES_URL+"/1")
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .param("mark", String.valueOf(gradeDTO.mark()))
                        .param("dateOfMark", String.valueOf(gradeDTO.dateOfMark()))
                        .param("comment", gradeDTO.comment())
                        .with(csrf()))
                .andExpect(status().is(HttpStatus.OK.value()))
                .andExpect(view().name("error/400"));
    }

    @SneakyThrows
    private void checkPostCreateInvalidGrade(GradeDTO gradeDTO) {
        mockMvc.perform(MockMvcRequestBuilders.post(GRADES_URL)
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .param("mark", String.valueOf(gradeDTO.mark()))
                        .param("dateOfMark", String.valueOf(gradeDTO.dateOfMark()))
                        .param("comment", gradeDTO.comment())
                        .param("studentId", String.valueOf(gradeDTO.studentId()))
                        .param("subjectId", String.valueOf(gradeDTO.subjectId()))
                        .with(csrf()))
                .andExpect(status().is(HttpStatus.OK.value()))
                .andExpect(view().name("error/400"));
    }
}
