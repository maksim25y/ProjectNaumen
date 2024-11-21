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
import ru.mudan.dto.homework.HomeworkCreateDTO;
import ru.mudan.dto.homework.HomeworkDTO;
import ru.mudan.exceptions.base.ApplicationForbiddenException;
import ru.mudan.exceptions.entity.not_found.ClassEntityNotFoundException;
import ru.mudan.exceptions.entity.not_found.HomeworkNotFoundException;
import ru.mudan.exceptions.entity.not_found.SubjectNotFoundException;
import ru.mudan.services.auth.AuthService;
import ru.mudan.services.classes.ClassService;
import ru.mudan.services.homework.HomeworkService;
import ru.mudan.services.subjects.SubjectService;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static ru.mudan.UtilConstants.*;

public class HomeworkControllerTest extends BaseControllerTest {

    private static final Supplier<Stream<Arguments>> invalidHomeworkDTOsForUpdate = () -> Stream.of(
            Arguments.of(new HomeworkDTO(null, "", "На странице 5", LocalDate.now(), null, null)),
            Arguments.of(new HomeworkDTO(null, null, "На странице 5", LocalDate.now(), null, null)),
            Arguments.of(new HomeworkDTO(null, "Решить примеры", "", LocalDate.now(), null, null)),
            Arguments.of(new HomeworkDTO(null, "Решить примеры", null, LocalDate.now(), null, null)),
            Arguments.of(new HomeworkDTO(null, "Решить примеры", "На странице 5", null, null, null))
            );

    private static final Supplier<Stream<Arguments>> invalidHomeworkCreateDTOs = () -> Stream.of(
            Arguments.of(new HomeworkCreateDTO("Решить примеры","На странице 5", LocalDate.now(), 1L)),
            Arguments.of(new HomeworkCreateDTO("","На странице 5", LocalDate.now(), 1L)),
            Arguments.of(new HomeworkCreateDTO(null,"На странице 5", LocalDate.now(), 1L)),
            Arguments.of(new HomeworkCreateDTO("Решить примеры","", LocalDate.now(), 1L)),
            Arguments.of(new HomeworkCreateDTO("Решить примеры",null, LocalDate.now(), 1L)),
            Arguments.of(new HomeworkCreateDTO("Решить примеры","На странице 5", null, 1L)),
            Arguments.of(new HomeworkCreateDTO("Решить примеры","На странице 5", LocalDate.now(), null))
            );

    public static Stream<Arguments> provideInvalidHomeworkDTOsForUpdate() {
        return invalidHomeworkDTOsForUpdate.get();
    }

    public static Stream<Arguments> provideInvalidHomeworkDTOsForCreate() {
        return invalidHomeworkCreateDTOs.get();
    }

    @MockBean
    private HomeworkService homeworkService;
    @MockBean
    private ClassService classService;
    @MockBean
    private SubjectService subjectService;
    @MockBean
    private AuthService authService;
    @MockBean
    private MessageSource messageSource;

    @Test
    @SneakyThrows
    @WithMockUser(roles = "ADMIN")
    @DisplayName("Should return status 200 and all homeworks for class")
    public void getPageWithAllHomeworkByExistedSubjectInExistedClass_roleAdmin() {
        when(classService.findById(any())).thenReturn(getDefaultClassDTO());
        when(subjectService.findById(any())).thenReturn(getDefaultSubjectDTO());
        when(homeworkService.findAllByClassAndSubject(any(), any())).thenReturn(List.of(getDefaultHomeworkDTO()));

        mockMvc.perform(MockMvcRequestBuilders.get(HOMEWORKS_URL + "/all/1?subjectId=1")
                        .accept(MediaType.TEXT_HTML).with(csrf()))
                .andExpect(status().is(HttpStatus.OK.value()))
                .andExpect(model().attributeExists("class"))
                .andExpect(model().attributeExists("homeworks"))
                .andExpect(model().attributeExists("subject"));
    }

    @Test
    @SneakyThrows
    @WithMockUser(roles = "ADMIN")
    @DisplayName("Should return status 200 and all homeworks for class")
    public void getPageWithAllHomeworkByNotExistedSubjectInExistedClass_roleAdmin() {
        when(classService.findById(any())).thenReturn(getDefaultClassDTO());
        when(subjectService.findById(any())).thenThrow(SubjectNotFoundException.class);

        mockMvc.perform(MockMvcRequestBuilders.get(HOMEWORKS_URL + "/all/1?subjectId=1")
                        .accept(MediaType.TEXT_HTML).with(csrf()))
                .andExpect(status().is(HttpStatus.OK.value()))
                .andExpect(view().name("error/error-app"));
    }

    @Test
    @SneakyThrows
    @WithMockUser(roles = "ADMIN")
    @DisplayName("Should return status 200 and all homeworks for class")
    public void getPageWithAllHomeworkByExistedSubjectInNotExistedClass_roleAdmin() {
        when(classService.findById(any())).thenThrow(ClassEntityNotFoundException.class);

        mockMvc.perform(MockMvcRequestBuilders.get(HOMEWORKS_URL + "/all/1?subjectId=1")
                        .accept(MediaType.TEXT_HTML).with(csrf()))
                .andExpect(status().is(HttpStatus.OK.value()))
                .andExpect(view().name("error/error-app"));
    }

    @Test
    @SneakyThrows
    @WithMockUser(roles = "STUDENT")
    @DisplayName("Should return status 200 and all homeworks for class")
    public void getPageWithAllHomeworkByExistedSubjectInExistedClass_roleStudentOfClass() {
        when(classService.findById(any())).thenReturn(getDefaultClassDTO());
        when(subjectService.findById(any())).thenReturn(getDefaultSubjectDTO());
        when(homeworkService.findAllByClassAndSubject(any(), any())).thenReturn(List.of(getDefaultHomeworkDTO()));

        mockMvc.perform(MockMvcRequestBuilders.get(HOMEWORKS_URL + "/all/1?subjectId=1")
                        .accept(MediaType.TEXT_HTML).with(csrf()))
                .andExpect(status().is(HttpStatus.OK.value()))
                .andExpect(model().attributeExists("class"))
                .andExpect(model().attributeExists("homeworks"))
                .andExpect(model().attributeExists("subject"));
    }

    @Test
    @SneakyThrows
    @WithMockUser(roles = "PARENT")
    @DisplayName("Should return status 200 and all homeworks for class")
    public void getPageWithAllHomeworkByExistedSubjectInExistedClass_roleParentOfStudentInClass() {
        when(classService.findById(any())).thenReturn(getDefaultClassDTO());
        when(subjectService.findById(any())).thenReturn(getDefaultSubjectDTO());
        when(homeworkService.findAllByClassAndSubject(any(), any())).thenReturn(List.of(getDefaultHomeworkDTO()));

        mockMvc.perform(MockMvcRequestBuilders.get(HOMEWORKS_URL + "/all/1?subjectId=1")
                        .accept(MediaType.TEXT_HTML).with(csrf()))
                .andExpect(status().is(HttpStatus.OK.value()))
                .andExpect(model().attributeExists("class"))
                .andExpect(model().attributeExists("homeworks"))
                .andExpect(model().attributeExists("subject"));
    }

    @Test
    @SneakyThrows
    @WithMockUser(roles = "PARENT")
    @DisplayName("Should return status 200 and all homeworks for class")
    public void getPageWithAllHomeworkByExistedSubjectInExistedClass_roleParentOfStudentThatNotFromClass() {
        doThrow(ApplicationForbiddenException.class).when(authService).hasRoleAdminOrStudentFromClassOrParentThatHasStudentInClass(any(),any());

        mockMvc.perform(MockMvcRequestBuilders.get(HOMEWORKS_URL + "/all/1?subjectId=1")
                        .accept(MediaType.TEXT_HTML).with(csrf()))
                .andExpect(status().is(HttpStatus.OK.value()))
                .andExpect(view().name("error/403"));
    }

    @Test
    @SneakyThrows
    @WithMockUser(roles = "PARENT")
    @DisplayName("Should return status 200 and all homeworks for class")
    public void getPageWithAllHomeworkByNotExistedSubjectInExistedClass_roleParentOfStudentOfClass() {
        when(classService.findById(any())).thenReturn(getDefaultClassDTO());
        when(subjectService.findById(any())).thenThrow(SubjectNotFoundException.class);

        mockMvc.perform(MockMvcRequestBuilders.get(HOMEWORKS_URL + "/all/1?subjectId=1")
                        .accept(MediaType.TEXT_HTML).with(csrf()))
                .andExpect(status().is(HttpStatus.OK.value()))
                .andExpect(view().name("error/error-app"));
    }

    @Test
    @SneakyThrows
    @WithMockUser(roles = "PARENT")
    @DisplayName("Should return status 200 and all homeworks for class")
    public void getPageWithAllHomeworkByExistedSubjectInNotExistedClass_roleParentOfStudentOfClass() {
        when(classService.findById(any())).thenThrow(ClassEntityNotFoundException.class);

        mockMvc.perform(MockMvcRequestBuilders.get(HOMEWORKS_URL + "/all/1?subjectId=1")
                        .accept(MediaType.TEXT_HTML).with(csrf()))
                .andExpect(status().is(HttpStatus.OK.value()))
                .andExpect(view().name("error/error-app"));
    }

    @Test
    @SneakyThrows
    @WithMockUser(roles = "STUDENT")
    @DisplayName("Should return status 200 and error page")
    public void getPageWithAllHomeworkByExistedSubjectInExistedClass_roleStudentNotFromClass() {
        doThrow(ApplicationForbiddenException.class).when(authService).hasRoleAdminOrStudentFromClassOrParentThatHasStudentInClass(any(),any());

        mockMvc.perform(MockMvcRequestBuilders.get(HOMEWORKS_URL + "/all/1?subjectId=1")
                        .accept(MediaType.TEXT_HTML).with(csrf()))
                .andExpect(status().is(HttpStatus.OK.value()))
                .andExpect(view().name("error/403"));
    }

    @Test
    @SneakyThrows
    @WithMockUser(roles = "STUDENT")
    @DisplayName("Should return status 200 and all homeworks for class")
    public void getPageWithAllHomeworkByNotExistedSubjectInExistedClass_roleStudentFromClass() {
        when(homeworkService.findAllByClassAndSubject(any(), any())).thenThrow(SubjectNotFoundException.class);

        mockMvc.perform(MockMvcRequestBuilders.get(HOMEWORKS_URL + "/all/1?subjectId=1")
                        .accept(MediaType.TEXT_HTML).with(csrf()))
                .andExpect(status().is(HttpStatus.OK.value()))
                .andExpect(view().name("error/error-app"));
    }

    @Test
    @SneakyThrows
    @WithMockUser(roles = "TEACHER")
    @DisplayName("Should return status 200 and all homeworks for class")
    public void getPageWithAllHomeworkByExistedSubjectInExistedClass_roleTeacher() {
        doThrow(ApplicationForbiddenException.class).when(authService).hasRoleAdminOrStudentFromClassOrParentThatHasStudentInClass(any(), any());

        mockMvc.perform(MockMvcRequestBuilders.get(HOMEWORKS_URL + "/all/1?subjectId=1")
                        .accept(MediaType.TEXT_HTML).with(csrf()))
                .andExpect(status().is(HttpStatus.OK.value()))
                .andExpect(view().name("error/403"));
    }

    @Test
    @SneakyThrows
    @WithMockUser(roles = "ADMIN")
    @DisplayName("Should return status 200 and all homeworks for class")
    public void getPageWithAllHomeworkInExistedClass_roleAdmin() {
        when(classService.findById(any())).thenReturn(getDefaultClassDTO());
        when(homeworkService.findAllByClass(any())).thenReturn(List.of(getDefaultHomeworkDTO()));

        mockMvc.perform(MockMvcRequestBuilders.get(HOMEWORKS_URL + "/all/1")
                        .accept(MediaType.TEXT_HTML).with(csrf()))
                .andExpect(status().is(HttpStatus.OK.value()))
                .andExpect(model().attributeExists("class"))
                .andExpect(model().attributeExists("homeworks"));
    }

    @Test
    @SneakyThrows
    @WithMockUser(roles = "ADMIN")
    @DisplayName("Should return status 200 and all homeworks for class")
    public void getPageWithAllHomeworkInNotExistedClass_roleAdmin() {
        when(classService.findById(any())).thenThrow(ClassEntityNotFoundException.class);

        mockMvc.perform(MockMvcRequestBuilders.get(HOMEWORKS_URL + "/all/1")
                        .accept(MediaType.TEXT_HTML).with(csrf()))
                .andExpect(status().is(HttpStatus.OK.value()))
                .andExpect(view().name("error/error-app"));
    }

    @Test
    @SneakyThrows
    @WithMockUser(roles = "STUDENT")
    @DisplayName("Should return status 200 and all homeworks for class")
    public void getPageWithAllHomeworkInExistedClass_roleStudentOfClass() {
        when(classService.findById(any())).thenReturn(getDefaultClassDTO());
        when(homeworkService.findAllByClass(any())).thenReturn(List.of(getDefaultHomeworkDTO()));

        mockMvc.perform(MockMvcRequestBuilders.get(HOMEWORKS_URL + "/all/1")
                        .accept(MediaType.TEXT_HTML).with(csrf()))
                .andExpect(status().is(HttpStatus.OK.value()))
                .andExpect(model().attributeExists("class"))
                .andExpect(model().attributeExists("homeworks"));
    }

    @Test
    @SneakyThrows
    @WithMockUser(roles = "PARENT")
    @DisplayName("Should return status 200 and all homeworks for class")
    public void getPageWithAllHomeworkInExistedClass_roleParentOfStudentInClass() {
        when(classService.findById(any())).thenReturn(getDefaultClassDTO());
        when(homeworkService.findAllByClass(any())).thenReturn(List.of(getDefaultHomeworkDTO()));

        mockMvc.perform(MockMvcRequestBuilders.get(HOMEWORKS_URL + "/all/1")
                        .accept(MediaType.TEXT_HTML).with(csrf()))
                .andExpect(status().is(HttpStatus.OK.value()))
                .andExpect(model().attributeExists("class"))
                .andExpect(model().attributeExists("homeworks"));
    }

    @Test
    @SneakyThrows
    @WithMockUser(roles = "PARENT")
    @DisplayName("Should return status 200 and all homeworks for class")
    public void getPageWithAllHomeworkInExistedClass_roleParentOfStudentThatNotFromClass() {
        doThrow(ApplicationForbiddenException.class).when(authService).hasRoleAdminOrStudentFromClassOrParentThatHasStudentInClass(any(),any());

        mockMvc.perform(MockMvcRequestBuilders.get(HOMEWORKS_URL + "/all/1")
                        .accept(MediaType.TEXT_HTML).with(csrf()))
                .andExpect(status().is(HttpStatus.OK.value()))
                .andExpect(view().name("error/403"));
    }

    @Test
    @SneakyThrows
    @WithMockUser(roles = "PARENT")
    @DisplayName("Should return status 200 and all homeworks for class")
    public void getPageWithAllHomeworkInNotExistedClass_roleParentOfStudentOfClass() {
        when(classService.findById(any())).thenThrow(ClassEntityNotFoundException.class);

        mockMvc.perform(MockMvcRequestBuilders.get(HOMEWORKS_URL + "/all/1")
                        .accept(MediaType.TEXT_HTML).with(csrf()))
                .andExpect(status().is(HttpStatus.OK.value()))
                .andExpect(view().name("error/error-app"));
    }

    @Test
    @SneakyThrows
    @WithMockUser(roles = "STUDENT")
    @DisplayName("Should return status 200 and all homeworks for class")
    public void getPageWithAllHomeworkInExistedClass_roleStudentNotFromClass() {
        doThrow(ApplicationForbiddenException.class).when(authService).hasRoleAdminOrStudentFromClassOrParentThatHasStudentInClass(any(),any());

        mockMvc.perform(MockMvcRequestBuilders.get(HOMEWORKS_URL + "/all/1")
                        .accept(MediaType.TEXT_HTML).with(csrf()))
                .andExpect(status().is(HttpStatus.OK.value()))
                .andExpect(view().name("error/403"));
    }

    @Test
    @SneakyThrows
    @WithMockUser(roles = "TEACHER")
    @DisplayName("Should return status 200 and all page with error")
    public void getPageWithAllHomeworkInExistedClass_roleTeacher() {
        doThrow(ApplicationForbiddenException.class).when(authService).hasRoleAdminOrStudentFromClassOrParentThatHasStudentInClass(any(), any());

        mockMvc.perform(MockMvcRequestBuilders.get(HOMEWORKS_URL + "/all/1")
                        .accept(MediaType.TEXT_HTML).with(csrf()))
                .andExpect(status().is(HttpStatus.OK.value()))
                .andExpect(view().name("error/403"));
    }

    @Test
    @SneakyThrows
    @WithMockUser(roles = "ADMIN")
    @DisplayName("Should return status 200 and error page")
    public void getHomeworkByIdWithRoleAdminAndHomeworkDoesNotExists() {
        when(homeworkService.findById(any())).thenThrow((HomeworkNotFoundException.class));
        when(messageSource.getMessage(any(), any(), any())).thenReturn("ДЗ с id=1 не найдено");

        mockMvc.perform(MockMvcRequestBuilders.get(HOMEWORKS_URL + "/1")
                        .accept(MediaType.TEXT_HTML).with(csrf()))
                .andExpect(status().is(HttpStatus.OK.value()))
                .andExpect(view().name("error/error-app"))
                .andExpect(model().attributeExists("error"));
    }

    @Test
    @SneakyThrows
    @WithMockUser(roles = "ADMIN")
    @DisplayName("Should return status 200 and error page")
    public void getHomeworkByIdWithRoleAdminAndHomeworkExists() {
        when(homeworkService.findById(any())).thenReturn((getDefaultHomeworkDTO()));
        when(classService.findById(any())).thenReturn(getDefaultClassDTO());
        when(subjectService.findById(any())).thenReturn(getDefaultSubjectDTO());

        mockMvc.perform(MockMvcRequestBuilders.get(HOMEWORKS_URL + "/1")
                        .accept(MediaType.TEXT_HTML).with(csrf()))
                .andExpect(status().is(HttpStatus.OK.value()))
                .andExpect(view().name("admin/homeworks/homeworks-show"))
                .andExpect(model().attributeExists("homework"))
                .andExpect(model().attributeExists("class"))
                .andExpect(model().attributeExists("subject"));
    }

    @Test
    @SneakyThrows
    @WithMockUser(roles = "TEACHER")
    @DisplayName("Should return status 200 and error page")
    public void getHomeworkByIdWithRoleTeacherBySubjectAndHomeworkDoesNotExists() {
        when(homeworkService.findById(any())).thenThrow((HomeworkNotFoundException.class));
        when(messageSource.getMessage(any(), any(), any())).thenReturn("ДЗ с id=1 не найдено");

        mockMvc.perform(MockMvcRequestBuilders.get(HOMEWORKS_URL + "/1")
                        .accept(MediaType.TEXT_HTML).with(csrf()))
                .andExpect(status().is(HttpStatus.OK.value()))
                .andExpect(view().name("error/error-app"))
                .andExpect(model().attributeExists("error"));
    }

    @Test
    @SneakyThrows
    @WithMockUser(roles = "TEACHER")
    @DisplayName("Should return status 200 and error page")
    public void getHomeworkByIdWithRoleTeacherBySubjectAndHomeworkExists() {
        when(homeworkService.findById(any())).thenReturn((getDefaultHomeworkDTO()));
        when(classService.findById(any())).thenReturn(getDefaultClassDTO());
        when(subjectService.findById(any())).thenReturn(getDefaultSubjectDTO());

        mockMvc.perform(MockMvcRequestBuilders.get(HOMEWORKS_URL + "/1")
                        .accept(MediaType.TEXT_HTML).with(csrf()))
                .andExpect(status().is(HttpStatus.OK.value()))
                .andExpect(view().name("admin/homeworks/homeworks-show"))
                .andExpect(model().attributeExists("homework"))
                .andExpect(model().attributeExists("class"))
                .andExpect(model().attributeExists("subject"));
    }

    @Test
    @SneakyThrows
    @WithMockUser(roles = "TEACHER")
    @DisplayName("Should return status 200 and error page")
    public void getHomeworkByIdWithRoleTeacherHasNotSubjectAndHomeworkExists() {
        doThrow(ApplicationForbiddenException.class).when(authService).teacherHasHomeworkOrRoleIsAdmin(any(), any());

        mockMvc.perform(MockMvcRequestBuilders.get(HOMEWORKS_URL + "/1")
                        .accept(MediaType.TEXT_HTML).with(csrf()))
                .andExpect(status().is(HttpStatus.OK.value()))
                .andExpect(view().name("error/403"));
    }

    @Test
    @SneakyThrows
    @WithMockUser(roles = "ADMIN")
    @DisplayName("Should return status 302 and update homework")
    public void putUpdateHomeworkValid_roleAdmin() {
        var payload = getDefaultHomeworkDTO();

        mockMvc.perform(MockMvcRequestBuilders.put(HOMEWORKS_URL + "/1")
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .param("title", payload.title())
                        .param("deadline", String.valueOf(payload.deadline()))
                        .param("description", payload.description())
                        .with(csrf()))
                .andExpect(status().is(HttpStatus.FOUND.value()));
    }

    @Test
    @SneakyThrows
    @WithMockUser(roles = "ADMIN")
    @DisplayName("Should return status 200 and error page")
    public void putUpdateNotExistedHomeworkValid_roleAdmin() {
        var payload = getDefaultHomeworkDTO();
        doThrow(HomeworkNotFoundException.class).when(homeworkService).update(any(), any());
        when(messageSource.getMessage(any(), any())).thenReturn("ДЗ с id=1 не найдено");

        mockMvc.perform(MockMvcRequestBuilders.put(HOMEWORKS_URL + "/1")
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .param("title", payload.title())
                        .param("deadline", String.valueOf(payload.deadline()))
                        .param("description", payload.description())
                        .with(csrf()))
                .andExpect(status().is(HttpStatus.OK.value()))
                .andExpect(view().name("error/error-app"));
    }

    @Test
    @SneakyThrows
    @WithMockUser(roles = "ADMIN")
    @DisplayName("Should return status 200 and create new homework")
    public void postCreateHomeworkValid_roleAdmin() {
        var payload = HomeworkCreateDTO
                .builder()
                .title("Решить примеры")
                .description("На странице 5")
                .deadline(LocalDate.now())
                .subjectId(1L)
                .build();

        mockMvc.perform(MockMvcRequestBuilders.post(HOMEWORKS_URL)
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .param("title", payload.title())
                        .param("deadline", payload.deadline().toString())
                        .param("description", payload.description())
                        .param("subjectId", payload.subjectId().toString())
                        .with(csrf()))
                .andExpect(status().is(HttpStatus.FOUND.value()));
    }

    @SneakyThrows
    @ParameterizedTest
    @WithMockUser(roles = "ADMIN")
    @DisplayName("Should return status 200 and errors")
    @MethodSource("provideInvalidHomeworkDTOsForCreate")
    public void postCreateHomeworkInvalid(HomeworkCreateDTO homeworkCreateDTO) {
        checkPostCreateInvalidHomework(homeworkCreateDTO);
    }

    @SneakyThrows
    @ParameterizedTest
    @WithMockUser(roles = "ADMIN")
    @DisplayName("Should return status 200 and errors")
    @MethodSource("provideInvalidHomeworkDTOsForUpdate")
    public void putUpdateHomeworkInvalid(HomeworkDTO homeworkDTO) {
        checkPutUpdateInvalidHomework(homeworkDTO);
    }

    @Test
    @SneakyThrows
    @WithMockUser(roles = "ADMIN")
    @DisplayName("Should return status 200 and page for editing existed homework")
    public void getPageForEditingExistingHomework_roleAdmin() {
        when(homeworkService.findById(any())).thenReturn(getDefaultHomeworkDTO());

        mockMvc.perform(MockMvcRequestBuilders.get(HOMEWORKS_URL + "/1/edit")
                        .accept(MediaType.TEXT_HTML).with(csrf()))
                .andExpect(status().is(HttpStatus.OK.value()))
                .andExpect(view().name("admin/homeworks/homeworks-edit"))
                .andExpect(model().attributeExists("homework"));
    }

    @Test
    @SneakyThrows
    @WithMockUser(roles = "TEACHER")
    @DisplayName("Should return status 200 and page for editing existed homework")
    public void getPageForEditingExistingHomework_roleTeacherBySubject() {
        when(homeworkService.findById(any())).thenReturn(getDefaultHomeworkDTO());

        mockMvc.perform(MockMvcRequestBuilders.get(HOMEWORKS_URL + "/1/edit")
                        .accept(MediaType.TEXT_HTML).with(csrf()))
                .andExpect(status().is(HttpStatus.OK.value()))
                .andExpect(view().name("admin/homeworks/homeworks-edit"))
                .andExpect(model().attributeExists("homework"));
    }

    @Test
    @SneakyThrows
    @WithMockUser(roles = "TEACHER")
    @DisplayName("Should return status 200 and page with error")
    public void getPageForEditingExistingHomework_roleTeacherHasNotSubject() {
        doThrow(ApplicationForbiddenException.class).when(authService).teacherHasHomeworkOrRoleIsAdmin(any(), any());

        mockMvc.perform(MockMvcRequestBuilders.get(HOMEWORKS_URL + "/1/edit")
                        .accept(MediaType.TEXT_HTML).with(csrf()))
                .andExpect(status().is(HttpStatus.OK.value()))
                .andExpect(view().name("error/403"));
    }

    @Test
    @SneakyThrows
    @WithMockUser(roles = "ADMIN")
    @DisplayName("Should return status 302 and page for delete existed homework")
    public void deleteExistedHomework_roleAdmin() {
        mockMvc.perform(MockMvcRequestBuilders.delete(HOMEWORKS_URL + "/1")
                        .accept(MediaType.TEXT_HTML).with(csrf()))
                .andExpect(status().is(HttpStatus.FOUND.value()));
    }

    @Test
    @SneakyThrows
    @WithMockUser(roles = "ADMIN")
    @DisplayName("Should return status 200 and error")
    public void deleteNotExistedHomework_roleAdmin() {
        doThrow(HomeworkNotFoundException.class).when(homeworkService).delete(any());
        when(messageSource.getMessage(any(), any(), any())).thenReturn("ДЗ c id=1 не найдено");

        mockMvc.perform(MockMvcRequestBuilders.delete(HOMEWORKS_URL + "/1")
                        .accept(MediaType.TEXT_HTML).with(csrf()))
                .andExpect(status().is(HttpStatus.OK.value()))
                .andExpect(view().name("error/error-app"))
                .andExpect(model().attributeExists("error"));
    }

    @Test
    @SneakyThrows
    @WithMockUser(roles = "TEACHER")
    @DisplayName("Should return status 302 and page for delete existed homework")
    public void deleteExistedHomework_roleTeacher() {
        mockMvc.perform(MockMvcRequestBuilders.delete(HOMEWORKS_URL + "/1")
                        .accept(MediaType.TEXT_HTML).with(csrf()))
                .andExpect(status().is(HttpStatus.FOUND.value()));
    }

    @Test
    @SneakyThrows
    @WithMockUser(roles = "TEACHER")
    @DisplayName("Should return status 200 and error")
    public void deleteNotExistedHomework_roleTeacher() {
        doThrow(HomeworkNotFoundException.class).when(homeworkService).delete(any());
        when(messageSource.getMessage(any(), any(), any())).thenReturn("ДЗ c id=1 не найдено");

        mockMvc.perform(MockMvcRequestBuilders.delete(HOMEWORKS_URL + "/1")
                        .accept(MediaType.TEXT_HTML).with(csrf()))
                .andExpect(status().is(HttpStatus.OK.value()))
                .andExpect(view().name("error/error-app"))
                .andExpect(model().attributeExists("error"));
    }

    @SneakyThrows
    private void checkPutUpdateInvalidHomework(HomeworkDTO homeworkDTO) {
        mockMvc.perform(MockMvcRequestBuilders.put(HOMEWORKS_URL + "/1")
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .param("title", homeworkDTO.title())
                        .param("deadline", String.valueOf(homeworkDTO.deadline()))
                        .param("description", homeworkDTO.description())
                        .with(csrf()))
                .andExpect(status().is(HttpStatus.OK.value()))
                .andExpect(view().name("error/400"));
    }

    @SneakyThrows
    private void checkPostCreateInvalidHomework(HomeworkCreateDTO homeworkCreateDTO) {
        mockMvc.perform(MockMvcRequestBuilders.post(HOMEWORKS_URL)
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .param("title", homeworkCreateDTO.title())
                        .param("deadline", String.valueOf(homeworkCreateDTO.deadline()))
                        .param("description", homeworkCreateDTO.description())
                        .with(csrf()))
                .andExpect(status().is(HttpStatus.OK.value()))
                .andExpect(view().name("error/400"));
    }
}