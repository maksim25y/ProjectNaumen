package ru.mudan.controllers;

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
import ru.mudan.dto.subjects.SubjectCreateDTO;
import ru.mudan.dto.subjects.SubjectUpdateDTO;
import ru.mudan.exceptions.entity.not_found.SubjectNotFoundException;
import ru.mudan.services.classes.ClassService;
import ru.mudan.services.subjects.SubjectService;
import ru.mudan.services.teachers.TeacherService;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static ru.mudan.controllers.UtilConstants.SUBJECTS_URL;
import static ru.mudan.controllers.UtilConstants.getDefaultSubjectDTO;

@WithMockUser(roles = "ADMIN")
public class SubjectControllerTest extends BaseControllerTest {

    private static final Supplier<Stream<Arguments>> invalidSubjectCreateDTOs = () -> Stream.of(
            Arguments.of(new SubjectCreateDTO(null, "Базовый", "Предмет про числа", 1L, 1L)),
            Arguments.of(new SubjectCreateDTO("", "Базовый", "Предмет про числа", 1L, 1L)),
            Arguments.of(new SubjectCreateDTO("Математикаматематикам", "Базовый", "Предмет про числа", 1L, 1L)),
            Arguments.of(new SubjectCreateDTO("математика", "Базовый", "Предмет про числа", 1L, 1L)),
            Arguments.of(new SubjectCreateDTO("Math", "Базовый", "Предмет про числа", 1L, 1L)),
            Arguments.of(new SubjectCreateDTO("Математика", "", "Предмет про числа", 1L, 1L)),
            Arguments.of(new SubjectCreateDTO("Математика", null, "Предмет про числа", 1L, 1L)),
            Arguments.of(new SubjectCreateDTO("Математика", "Базовый", "Предмет про числа", null, 1L)),
            Arguments.of(new SubjectCreateDTO("Математика", "Базовый", "Предмет про числа", 1L, null))
    );

    private static final Supplier<Stream<Arguments>> invalidSubjectUpdateDTOs = () -> Stream.of(
            Arguments.of(new SubjectUpdateDTO("", "Предмет про числа")),
            Arguments.of(new SubjectUpdateDTO(null, "Предмет про числа"))
    );

    public static Stream<Arguments> provideInvalidSubjectCreateDTOs() {
        return invalidSubjectCreateDTOs.get();
    }

    public static Stream<Arguments> provideInvalidSubjectUpdateDTOs() {
        return invalidSubjectUpdateDTOs.get();
    }

    @MockBean
    private SubjectService subjectService;
    @MockBean
    private ClassService classService;
    @MockBean
    private TeacherService teacherService;
    @MockBean
    private MessageSource messageSource;

    @Test
    @SneakyThrows
    @DisplayName("Should return status 200 and all subjects")
    public void getAllSubjectsWithRoleAdmin() {
        when(subjectService.findAll()).thenReturn(List.of(getDefaultSubjectDTO()));

        mockMvc.perform(MockMvcRequestBuilders.get(SUBJECTS_URL + "/all")
                        .accept(MediaType.TEXT_HTML).with(csrf()))
                .andExpect(status().is(HttpStatus.OK.value()))
                .andExpect(model().attributeExists("subjects"));
    }

    @Test
    @SneakyThrows
    @DisplayName("Should return status 403")
    @WithMockUser(roles = "PARENT")
    public void getAllSubjectsWithRoleParent() {
        checkForbiddenGetMethod("/all");
    }

    @Test
    @SneakyThrows
    @DisplayName("Should return status 403")
    @WithMockUser(roles = "TEACHER")
    public void getAllSubjectsWithRoleTeacher() {
        checkForbiddenGetMethod("/all");
    }

    @Test
    @SneakyThrows
    @DisplayName("Should return status 403")
    @WithMockUser(roles = "STUDENT")
    public void getAllSubjectsWithRoleStudent() {
        checkForbiddenGetMethod("/all");
    }

    @Test
    @SneakyThrows
    @DisplayName("Should return status 200 and subject")
    public void getSubjectByIdWithRoleAdminAndSubjectExists() {
        when(subjectService.findById(any())).thenReturn((getDefaultSubjectDTO()));

        mockMvc.perform(MockMvcRequestBuilders.get(SUBJECTS_URL + "/1")
                        .accept(MediaType.TEXT_HTML).with(csrf()))
                .andExpect(status().is(HttpStatus.OK.value()))
                .andExpect(model().attributeExists("subject"));
    }

    @Test
    @SneakyThrows
    @DisplayName("Should return status 200 and error page")
    public void getSubjectByIdWithRoleAdminAndSubjectDoesNotExists() {
        when(subjectService.findById(any())).thenThrow((SubjectNotFoundException.class));
        when(messageSource.getMessage(any(), any(), any())).thenReturn("Предмет с id=1 не найден");

        mockMvc.perform(MockMvcRequestBuilders.get(SUBJECTS_URL + "/1")
                        .accept(MediaType.TEXT_HTML).with(csrf()))
                .andExpect(status().is(HttpStatus.OK.value()))
                .andExpect(view().name("error/error-app"))
                .andExpect(model().attributeExists("error"));
    }

    @Test
    @SneakyThrows
    @DisplayName("Should return status 403")
    @WithMockUser(roles = "PARENT")
    public void getSubjectByIdWithRoleParent() {
        checkForbiddenGetMethod("/1");
    }

    @Test
    @SneakyThrows
    @DisplayName("Should return status 403")
    @WithMockUser(roles = "TEACHER")
    public void getSubjectByIdWithRoleTeacher() {
        checkForbiddenGetMethod("/1");
    }

    @Test
    @SneakyThrows
    @DisplayName("Should return status 403")
    @WithMockUser(roles = "STUDENT")
    public void getSubjectByIdWithRoleStudent() {
        checkForbiddenGetMethod("/1");
    }

    @Test
    @SneakyThrows
    @DisplayName("Should return status 200 and page for adding new subject")
    public void getPageForAddingNewSubjectForRoleAdmin() {
        when(classService.findAll()).thenReturn(List.of());
        when(teacherService.findAll()).thenReturn(List.of());

        mockMvc.perform(MockMvcRequestBuilders.get(SUBJECTS_URL + "/add")
                        .accept(MediaType.TEXT_HTML).with(csrf()))
                .andExpect(status().is(HttpStatus.OK.value()))
                .andExpect(view().name("admin/subjects/subjects-add"))
                .andExpect(model().attributeExists("teachers"))
                .andExpect(model().attributeExists("classes"));
    }

    @Test
    @SneakyThrows
    @DisplayName("Should return status 403")
    @WithMockUser(roles = "STUDENT")
    public void gePageForAddingNewSubjectForRoleStudent() {
        checkForbiddenGetMethod("/add");
    }

    @Test
    @SneakyThrows
    @DisplayName("Should return status 403")
    @WithMockUser(roles = "PARENT")
    public void gePageForAddingNewSubjectForRoleParent() {
        checkForbiddenGetMethod("/add");
    }

    @Test
    @SneakyThrows
    @DisplayName("Should return status 403")
    @WithMockUser(roles = "TEACHER")
    public void gePageForAddingNewSubjectForRoleTeacher() {
        checkForbiddenGetMethod("/add");
    }

    @Test
    @SneakyThrows
    @DisplayName("Should return status 200 and create new subject")
    public void postCreateNewSubjectValid() {
        var payload = SubjectCreateDTO
                .builder()
                .name("Математика")
                .type("Базовый")
                .description("Предмет про числа")
                .classId(1L)
                .teacherId(1L)
                .build();

        postCreateSubjectValid(payload);
    }

    @SneakyThrows
    @ParameterizedTest
    @DisplayName("Should return status 200 and errors")
    @MethodSource("provideInvalidSubjectCreateDTOs")
    public void postCreateNewSubjectInvalid(SubjectCreateDTO subjectCreateDTO) {
        checkPostCreateInvalidSubject(subjectCreateDTO);
    }

    @Test
    @SneakyThrows
    @DisplayName("Should return status 200 and page for editing existed subject")
    public void getPageForEditingExistingSubjectForRoleAdmin() {
        when(subjectService.findById(any())).thenReturn(getDefaultSubjectDTO());

        mockMvc.perform(MockMvcRequestBuilders.get(SUBJECTS_URL + "/1/edit")
                        .accept(MediaType.TEXT_HTML).with(csrf()))
                .andExpect(status().is(HttpStatus.OK.value()))
                .andExpect(view().name("admin/subjects/subjects-edit"))
                .andExpect(model().attributeExists("subject"));
    }

    @Test
    @SneakyThrows
    @DisplayName("Should return status 200 and return error page")
    public void getPageForEditingNotExistingSubjectForRoleAdmin() {
        when(subjectService.findById(any())).thenThrow(SubjectNotFoundException.class);
        when(messageSource.getMessage(any(), any(), any())).thenReturn("Предмет c id=1 не найден");

        mockMvc.perform(MockMvcRequestBuilders.get(SUBJECTS_URL + "/1/edit")
                        .accept(MediaType.TEXT_HTML).with(csrf()))
                .andExpect(status().is(HttpStatus.OK.value()))
                .andExpect(view().name("error/error-app"))
                .andExpect(model().attributeExists("error"));
    }

    @Test
    @SneakyThrows
    @DisplayName("Should return status 403")
    @WithMockUser(roles = "STUDENT")
    public void getPageForEditingSubjectForRoleStudent() {
        checkForbiddenGetMethod("/1/edit");
    }

    @Test
    @SneakyThrows
    @DisplayName("Should return status 403")
    @WithMockUser(roles = "PARENT")
    public void getPageForEditingSubjectForRoleParent() {
        checkForbiddenGetMethod("/1/edit");
    }

    @Test
    @SneakyThrows
    @DisplayName("Should return status 403")
    @WithMockUser(roles = "TEACHER")
    public void getPageForEditingSubjectForRoleTeacher() {
        checkForbiddenGetMethod("/1/edit");
    }

    @Test
    @SneakyThrows
    @DisplayName("Should return status 302 and update subject")
    public void putUpdateSubjectValidForRoleAdmin() {
        var payload = SubjectUpdateDTO
                .builder()
                .type("Факультативный")
                .description("Тестовое описание")
                .build();

        putUpdateSubjectValid(payload);
    }

    @SneakyThrows
    @ParameterizedTest
    @DisplayName("Should return status 200 and errors")
    @MethodSource("provideInvalidSubjectUpdateDTOs")
    public void putUpdateSubjectInvalid(SubjectUpdateDTO subjectUpdateDTO) {
        checkPutUpdateInvalidSubject(subjectUpdateDTO);
    }

    @Test
    @SneakyThrows
    @DisplayName("Should return status 302 and page for delete existed subject")
    public void deleteExistedSubjectForRoleAdmin() {
        mockMvc.perform(MockMvcRequestBuilders.delete(SUBJECTS_URL + "/1")
                        .accept(MediaType.TEXT_HTML).with(csrf()))
                .andExpect(status().is(HttpStatus.FOUND.value()));
    }

    @Test
    @SneakyThrows
    @DisplayName("Should return status 200 and error")
    public void deleteNotExistedSubjectForRoleAdmin() {
        doThrow(SubjectNotFoundException.class).when(subjectService).deleteById(any());
        when(messageSource.getMessage(any(), any(), any())).thenReturn("Предмет c id=1 не найден");

        mockMvc.perform(MockMvcRequestBuilders.delete(SUBJECTS_URL + "/1")
                        .accept(MediaType.TEXT_HTML).with(csrf()))
                .andExpect(status().is(HttpStatus.OK.value()))
                .andExpect(view().name("error/error-app"))
                .andExpect(model().attributeExists("error"));
    }

    @SneakyThrows
    private void checkPostCreateInvalidSubject(SubjectCreateDTO subjectCreateDTO) {
        mockMvc.perform(MockMvcRequestBuilders.post(SUBJECTS_URL)
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .param("name", subjectCreateDTO.name())
                        .param("type", subjectCreateDTO.type())
                        .param("description", subjectCreateDTO.description())
                        .param("classId", String.valueOf(subjectCreateDTO.classId()))
                        .param("teacherId", String.valueOf(subjectCreateDTO.teacherId()))
                        .with(csrf()))
                .andExpect(status().is(HttpStatus.OK.value()))
                .andExpect(model().attributeExists("errors"));
    }

    @SneakyThrows
    private void checkForbiddenGetMethod(String path) {
        mockMvc.perform(MockMvcRequestBuilders.get(SUBJECTS_URL + path)
                        .accept(MediaType.TEXT_HTML).with(csrf()))
                .andExpect(status().is(HttpStatus.FORBIDDEN.value()));
    }

    @SneakyThrows
    private void checkPutUpdateInvalidSubject(SubjectUpdateDTO subjectUpdateDTO) {
        mockMvc.perform(MockMvcRequestBuilders.put(SUBJECTS_URL + "/1")
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .param("type", subjectUpdateDTO.type())
                        .param("description", subjectUpdateDTO.description())
                        .with(csrf()))
                .andExpect(status().is(HttpStatus.OK.value()))
                .andExpect(model().attributeExists("errors"));
    }

    @SneakyThrows
    private void postCreateSubjectValid(SubjectCreateDTO subjectCreateDTO) {
        mockMvc.perform(MockMvcRequestBuilders.post(SUBJECTS_URL)
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .param("name", subjectCreateDTO.name())
                        .param("type", subjectCreateDTO.type())
                        .param("description", subjectCreateDTO.description())
                        .param("classId", String.valueOf(subjectCreateDTO.classId()))
                        .param("teacherId", String.valueOf(subjectCreateDTO.teacherId()))
                        .with(csrf()))
                .andExpect(status().is(HttpStatus.FOUND.value()));
    }

    @SneakyThrows
    private void putUpdateSubjectValid(SubjectUpdateDTO subjectUpdateDTO) {
        mockMvc.perform(MockMvcRequestBuilders.put(SUBJECTS_URL + "/1")
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .param("type", subjectUpdateDTO.type())
                        .param("description", subjectUpdateDTO.description())
                        .with(csrf()))
                .andExpect(status().is(HttpStatus.FOUND.value()));
    }
}
