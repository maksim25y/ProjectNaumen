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
import static ru.mudan.UtilConstants.*;

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
    public void getAllGradesForExistedStudentBySubject_roleAdmin() {
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
    public void getAllGradesForExistedStudentBySubject_roleCurrentStudent() {
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
    public void getAllGradesForNotExistedStudentBySubject_roleAdmin() {
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
    public void getAllGradesForExistedStudentBySubject_roleTeacher() {
        doThrow(ApplicationForbiddenException.class).when(authService).hasRoleAdminOrStudentInClassWithSubjectOrParentHasStudentInClass(any(), any(), any());

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
    public void getAllGradesForExistedStudentBySubject_roleNotCurrentStudent() {
        doThrow(ApplicationForbiddenException.class).when(authService).hasRoleAdminOrStudentInClassWithSubjectOrParentHasStudentInClass(any(), any(), any());

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
    public void getAllGradesForExistedStudentByExistedSubject_roleParent() {
        doThrow(ApplicationForbiddenException.class).when(authService).hasRoleAdminOrStudentInClassWithSubjectOrParentHasStudentInClass(any(), any(), any());

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
    public void getAllGradesForExistedStudent_roleParent() {
        doThrow(ApplicationForbiddenException.class).when(authService).hasRoleAdminOrStudentInClassWithSubjectOrParentHasStudentInClass(any(), any(), any());

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
    public void getAllGradesForExistedStudent_roleTeacher() {
        doThrow(ApplicationForbiddenException.class).when(authService).hasRoleAdminOrStudentInClassWithSubjectOrParentHasStudentInClass(any(), any(), any());

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
    public void getAllGradesForExistedStudent_roleAdmin() {
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
    public void getAllGradesForExistedStudent_roleCurrentStudent() {
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
    public void getAllGradesForNotExistedStudent_roleAdmin() {
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
    public void getExistedGradeById_roleAdmin() {
        when(gradesService.findById(any())).thenReturn(getDefaultGradeDTO());

        mockMvc.perform(MockMvcRequestBuilders.get(GRADES_URL + "/1")
                        .accept(MediaType.TEXT_HTML).with(csrf()))
                .andExpect(status().is(HttpStatus.OK.value()))
                .andExpect(model().attributeExists("grade"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    @SneakyThrows
    public void getNotExistedGradeById_roleAdmin() {
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
    public void getExistedGradeById_roleTeacherHasGrade() {
        when(gradesService.findById(any())).thenReturn(getDefaultGradeDTO());

        mockMvc.perform(MockMvcRequestBuilders.get(GRADES_URL + "/1")
                        .accept(MediaType.TEXT_HTML).with(csrf()))
                .andExpect(status().is(HttpStatus.OK.value()))
                .andExpect(model().attributeExists("grade"));
    }

    @Test
    @WithMockUser(roles = "TEACHER")
    @SneakyThrows
    public void getNotExistedGradeById_roleTeacherHasNotGrade() {
        doThrow(ApplicationForbiddenException.class).when(authService).teacherHasGradeOrRoleIsAdmin(any(), any());

        mockMvc.perform(MockMvcRequestBuilders.get(GRADES_URL + "/1")
                        .accept(MediaType.TEXT_HTML).with(csrf()))
                .andExpect(status().is(HttpStatus.OK.value()))
                .andExpect(view().name("error/403"));
    }

    @Test
    @WithMockUser(roles = "PARENT")
    @SneakyThrows
    public void getNotExistedGradeById_roleParent() {
        doThrow(ApplicationForbiddenException.class).when(authService).teacherHasGradeOrRoleIsAdmin(any(), any());

        mockMvc.perform(MockMvcRequestBuilders.get(GRADES_URL + "/1")
                        .accept(MediaType.TEXT_HTML).with(csrf()))
                .andExpect(status().is(HttpStatus.OK.value()))
                .andExpect(view().name("error/403"));
    }

    @Test
    @WithMockUser(roles = "STUDENT")
    @SneakyThrows
    public void getNotExistedGradeById_roleStudent() {
        doThrow(ApplicationForbiddenException.class).when(authService).teacherHasGradeOrRoleIsAdmin(any(), any());

        mockMvc.perform(MockMvcRequestBuilders.get(GRADES_URL + "/1")
                        .accept(MediaType.TEXT_HTML).with(csrf()))
                .andExpect(status().is(HttpStatus.OK.value()))
                .andExpect(view().name("error/403"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    @SneakyThrows
    public void getPageForEditingGrade_roleAdmin() {
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
    public void getPageForEditingNotExistedGrade_roleAdmin() {
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
    public void getPageForEditingGrade_roleTeacherHasGrade() {
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
    public void getPageForEditingGrade_roleTeacherHasNotGrade() {
        doThrow(ApplicationForbiddenException.class).when(authService).teacherHasGradeOrRoleIsAdmin(any(), any());

        mockMvc.perform(MockMvcRequestBuilders.get(GRADES_URL + "/1/edit")
                        .accept(MediaType.TEXT_HTML).with(csrf()))
                .andExpect(status().is(HttpStatus.OK.value()))
                .andExpect(view().name("error/403"));
    }

    @Test
    @WithMockUser(roles = "PARENT")
    @SneakyThrows
    public void getPageForEditingGrade_roleParent() {
        doThrow(ApplicationForbiddenException.class).when(authService).teacherHasGradeOrRoleIsAdmin(any(), any());

        mockMvc.perform(MockMvcRequestBuilders.get(GRADES_URL + "/1/edit")
                        .accept(MediaType.TEXT_HTML).with(csrf()))
                .andExpect(status().is(HttpStatus.OK.value()))
                .andExpect(view().name("error/403"));
    }

    @Test
    @WithMockUser(roles = "STUDENT")
    @SneakyThrows
    public void getPageForEditingGrade_roleStudent() {
        doThrow(ApplicationForbiddenException.class).when(authService).teacherHasGradeOrRoleIsAdmin(any(), any());

        mockMvc.perform(MockMvcRequestBuilders.get(GRADES_URL + "/1/edit")
                        .accept(MediaType.TEXT_HTML).with(csrf()))
                .andExpect(status().is(HttpStatus.OK.value()))
                .andExpect(view().name("error/403"));
    }

    @Test
    @SneakyThrows
    @WithMockUser(roles = "ADMIN")
    public void deleteExistedGrade_roleAdmin() {
        mockMvc.perform(MockMvcRequestBuilders.delete(GRADES_URL + "/1")
                        .accept(MediaType.TEXT_HTML).with(csrf()))
                .andExpect(status().is(HttpStatus.FOUND.value()));
    }

    @Test
    @SneakyThrows
    @WithMockUser(roles = "ADMIN")
    public void deleteNotExistedGrade_roleAdmin() {
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
    public void deleteExistedGrade_roleTeacherThatHasGrade() {
        mockMvc.perform(MockMvcRequestBuilders.delete(GRADES_URL + "/1")
                        .accept(MediaType.TEXT_HTML).with(csrf()))
                .andExpect(status().is(HttpStatus.FOUND.value()));
    }

    @Test
    @SneakyThrows
    @WithMockUser(roles = "TEACHER")
    public void deleteNotExistedGrade_roleTeacherThatHasNotGrade() {
        doThrow(ApplicationForbiddenException.class).when(authService).teacherHasGradeOrRoleIsAdmin(any(), any());

        mockMvc.perform(MockMvcRequestBuilders.delete(GRADES_URL + "/1")
                        .accept(MediaType.TEXT_HTML).with(csrf()))
                .andExpect(status().is(HttpStatus.OK.value()))
                .andExpect(view().name("error/403"));
    }

    @Test
    @WithMockUser(roles = "PARENT")
    @SneakyThrows
    public void deleteNotExistedGrade_roleParent() {
        doThrow(ApplicationForbiddenException.class).when(authService).teacherHasGradeOrRoleIsAdmin(any(), any());

        mockMvc.perform(MockMvcRequestBuilders.delete(GRADES_URL + "/1")
                        .accept(MediaType.TEXT_HTML).with(csrf()))
                .andExpect(status().is(HttpStatus.OK.value()))
                .andExpect(view().name("error/403"));
    }

    @Test
    @WithMockUser(roles = "STUDENT")
    @SneakyThrows
    public void deleteNotExistedGrade_roleStudent() {
        doThrow(ApplicationForbiddenException.class).when(authService).teacherHasGradeOrRoleIsAdmin(any(), any());

        mockMvc.perform(MockMvcRequestBuilders.delete(GRADES_URL + "/1")
                        .accept(MediaType.TEXT_HTML).with(csrf()))
                .andExpect(status().is(HttpStatus.OK.value()))
                .andExpect(view().name("error/403"));
    }

    @Test
    @SneakyThrows
    @WithMockUser(roles = "ADMIN")
    public void postCreateGradeValid_roleAdmin() {
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
    public void postCreateGradeValid_roleTeacherHasGrade() {
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
    public void postCreateGradeValid_roleTeacherHasNotGrade() {
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
    public void postCreateGradeValid_roleParent() {
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
    public void postCreateGradeValid_roleStudent() {
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
    public void putUpdateGradeValid_roleAdmin() {
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
    public void putUpdateGradeValid_roleTeacherThatHasGrade() {
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
    public void putUpdateGradeValid_roleParent() {
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
    public void putUpdateGradeValid_roleStudent() {
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
    public void putUpdateNotExistedGradeValid_roleAdmin() {
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
    public void postCreateGradeInvalid_roleAdmin(GradeDTO gradeDTO) {
        checkPostCreateInvalidGrade(gradeDTO);
    }

    @SneakyThrows
    @ParameterizedTest
    @WithMockUser(roles = "ADMIN")
    @DisplayName("Should return status 200 and errors")
    @MethodSource("provideInvalidGradeDTOs")
    public void putUpdateGradeInvalid_roleAdmin(GradeDTO gradeDTO) {
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
