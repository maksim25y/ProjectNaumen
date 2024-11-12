package ru.mudan.controllers;

import java.util.List;
import java.util.function.Supplier;
import java.util.stream.Collectors;
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
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import ru.mudan.dto.classes.ClassDTO;
import ru.mudan.exceptions.entity.not_found.ClassEntityNotFoundException;
import ru.mudan.exceptions.entity.not_found.StudentNotFoundException;
import ru.mudan.exceptions.entity.not_found.SubjectNotFoundException;
import ru.mudan.services.classes.ClassService;
import ru.mudan.services.students.StudentService;
import ru.mudan.services.subjects.SubjectService;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static ru.mudan.controllers.UtilConstants.CLASSES_URL;
import static ru.mudan.controllers.UtilConstants.*;

@WithMockUser(roles = "ADMIN")
public class ClassesControllerTest extends BaseControllerTest {

    private static final Supplier<Stream<Arguments>> invalidClassDTOs = () -> Stream.of(
            Arguments.of(new ClassDTO(1L,"А",0,"Тестовое описание",List.of(),List.of())),
            Arguments.of(new ClassDTO(1L,"А",12,"Тестовое описание",List.of(),List.of())),
            Arguments.of(new ClassDTO(1L,"G",6,"Тестовое описание",List.of(),List.of())),
            Arguments.of(new ClassDTO(1L,"Тест",6,"Тестовое описание",List.of(),List.of())),
            Arguments.of(new ClassDTO(1L,"Тест",6,"Тестовое описание",List.of(),List.of())),
            Arguments.of(new ClassDTO(1L,"Е",6,"Тестовое описание",List.of(),List.of())),
            Arguments.of(new ClassDTO(1L,"Е",6,"Тестовое описание",List.of(),List.of()))
    );

    public static Stream<Arguments> provideInvalidClassDTOs() {
        return invalidClassDTOs.get();
    }

    @MockBean
    private ClassService classService;
    @MockBean
    private SubjectService subjectService;
    @MockBean
    private StudentService studentService;
    @MockBean
    private MessageSource messageSource;

    @Test
    @SneakyThrows
    @DisplayName("Should return status 200 and all classes")
    public void getAllClassesWithRoleAdmin() {
        when(classService.findAll()).thenReturn(List.of(getDefaultClassDTO()));

        mockMvc.perform(MockMvcRequestBuilders.get(CLASSES_URL + "/all")
                        .accept(MediaType.TEXT_HTML).with(csrf()))
                .andExpect(status().is(HttpStatus.OK.value()))
                .andExpect(model().attributeExists("classes"));
    }

    @Test
    @SneakyThrows
    @DisplayName("Should return status 200 and existed class")
    public void getClassByIdWithRoleAdminAndClassExist() {
        when(classService.findById(any())).thenReturn(getDefaultClassDTO());
        when(studentService.findAllStudentsForClass(any())).thenReturn(List.of());
        when(subjectService.findAllSubjectsForClass(any())).thenReturn(List.of());
        when(studentService.findStudentsWithNotClass()).thenReturn(List.of());

        mockMvc.perform(MockMvcRequestBuilders.get(CLASSES_URL + "/1")
                        .accept(MediaType.TEXT_HTML).with(csrf()))
                .andExpect(status().is(HttpStatus.OK.value()))
                .andExpect(model().attributeExists("cl"))
                .andExpect(model().attributeExists("students"))
                .andExpect(model().attributeExists("subjects"))
                .andExpect(model().attributeExists("studentsForAdding"));
    }

    @Test
    @SneakyThrows
    @DisplayName("Should return status 200 and error")
    public void getClassByIdWithRoleAdminAndClassDoesNotExist() {
        when(classService.findById(any())).thenThrow(ClassEntityNotFoundException.class);
        when(messageSource.getMessage(any(), any(), any())).thenReturn("Класс с id=1 не найден");

        mockMvc.perform(MockMvcRequestBuilders.get(CLASSES_URL + "/1")
                        .accept(MediaType.TEXT_HTML).with(csrf()))
                .andExpect(status().is(HttpStatus.OK.value()))
                .andExpect(model().attributeExists("error"))
                .andExpect(model().attributeDoesNotExist("cl"))
                .andExpect(model().attributeDoesNotExist("students"))
                .andExpect(model().attributeDoesNotExist("subjects"))
                .andExpect(model().attributeDoesNotExist("studentsForAdding"));
    }

    @Test
    @SneakyThrows
    @WithMockUser(roles = "TEACHER")
    @DisplayName("Should return status 403")
    public void getClassByIdWithRoleTeacher() {
        mockMvc.perform(MockMvcRequestBuilders.get(CLASSES_URL + "/1")
                        .accept(MediaType.TEXT_HTML).with(csrf()))
                .andExpect(status().is(HttpStatus.FORBIDDEN.value()));
    }

    @Test
    @SneakyThrows
    @WithMockUser(roles = "PARENT")
    @DisplayName("Should return status 403")
    public void getClassByIdWithRoleParent() {
        mockMvc.perform(MockMvcRequestBuilders.get(CLASSES_URL + "/1")
                        .accept(MediaType.TEXT_HTML).with(csrf()))
                .andExpect(status().is(HttpStatus.FORBIDDEN.value()));
    }

    @Test
    @SneakyThrows
    @WithMockUser(roles = "PARENT")
    @DisplayName("Should return status 403")
    public void getClassByIdWithRoleStudent() {
        mockMvc.perform(MockMvcRequestBuilders.get(CLASSES_URL + "/1")
                        .accept(MediaType.TEXT_HTML).with(csrf()))
                .andExpect(status().is(HttpStatus.FORBIDDEN.value()));
    }

    @Test
    @SneakyThrows
    @DisplayName("Should return status 200 and error")
    public void postAddStudentsToClassWithRoleAdminAndClassDoesNotExist() {
        var payload = getDefaultClassDTO();
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();

        List<String> studentIdsAsString = payload.studentsIds().stream()
                .map(String::valueOf)
                .collect(Collectors.toList());

        params.addAll("studentsIds", studentIdsAsString);

        doThrow(ClassEntityNotFoundException.class).when(classService).addStudentsToClass(any(), any());
        when(messageSource.getMessage(any(), any(), any())).thenReturn("Класс с id=1 не найден");

        mockMvc.perform(MockMvcRequestBuilders.post(CLASSES_URL + "/1/students")
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .params(params)
                        .with(csrf()))
                .andExpect(status().is(HttpStatus.OK.value()))
                .andExpect(model().attributeExists("error"))
                .andExpect(model().attributeDoesNotExist("cl"))
                .andExpect(model().attributeDoesNotExist("students"))
                .andExpect(model().attributeDoesNotExist("subjects"))
                .andExpect(model().attributeDoesNotExist("studentsForAdding"));
    }

    @Test
    @SneakyThrows
    @DisplayName("Should return status 200 and error")
    public void postAddStudentsToClassWithRoleAdminAndStudentDoesNotExist() {
        var payload = getDefaultClassDTO();
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();

        List<String> studentIdsAsString = payload.studentsIds().stream()
                .map(String::valueOf)
                .collect(Collectors.toList());

        params.addAll("studentsIds", studentIdsAsString);

        doThrow(StudentNotFoundException.class).when(classService).addStudentsToClass(any(), any());
        when(messageSource.getMessage(any(), any(), any())).thenReturn("Ученик с id=1 не найден");

        mockMvc.perform(MockMvcRequestBuilders.post(CLASSES_URL + "/1/students")
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .params(params)
                        .with(csrf()))
                .andExpect(status().is(HttpStatus.OK.value()));
    }

    @Test
    @SneakyThrows
    @DisplayName("Should return status 302 and add student to class")
    public void postAddStudentsToClassWithRoleAdminAndStudentsExist() {
        var payload = getDefaultClassDTO();
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();

        List<String> studentIdsAsString = payload.studentsIds().stream()
                .map(String::valueOf)
                .collect(Collectors.toList());

        params.addAll("studentsIds", studentIdsAsString);

        mockMvc.perform(MockMvcRequestBuilders.post(CLASSES_URL + "/1/students")
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .params(params)
                        .with(csrf()))
                .andExpect(status().is(HttpStatus.FOUND.value()));
    }

    @Test
    @SneakyThrows
    @DisplayName("Should return status 200 and error")
    public void postAddSubjectsToClassWithRoleAdminAndSubjectDoesNotExist() {
        var payload = getDefaultClassDTO();
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();

        List<String> subjectsIdsAsString = payload.subjectsIds().stream()
                .map(String::valueOf)
                .toList();

        params.addAll("subjectsIds", subjectsIdsAsString);

        doThrow(SubjectNotFoundException.class).when(classService).addSubjectsToClass(any(), any());
        when(messageSource.getMessage(any(), any(), any())).thenReturn("Предмет с id=1 не найден");

        mockMvc.perform(MockMvcRequestBuilders.post(CLASSES_URL + "/1/subjects")
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .params(params)
                        .with(csrf()))
                .andExpect(status().is(HttpStatus.OK.value()))
                .andExpect(model().attributeExists("error"));
    }

    @Test
    @SneakyThrows
    @DisplayName("Should return status 200 and error")
    public void postAddSubjectsToClassWithRoleAdminAndClassDoesNotExist() {
        var payload = getDefaultClassDTO();
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();

        List<String> subjectsIdsAsString = payload.subjectsIds().stream()
                .map(String::valueOf)
                .toList();

        params.addAll("subjectsIds", subjectsIdsAsString);

        doThrow(ClassEntityNotFoundException.class).when(classService).addSubjectsToClass(any(), any());
        when(messageSource.getMessage(any(), any(), any())).thenReturn("Класс с id=1 не найден");

        mockMvc.perform(MockMvcRequestBuilders.post(CLASSES_URL + "/1/subjects")
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .params(params)
                        .with(csrf()))
                .andExpect(status().is(HttpStatus.OK.value()))
                .andExpect(model().attributeExists("error"))
                .andExpect(model().attributeDoesNotExist("cl"))
                .andExpect(model().attributeDoesNotExist("students"))
                .andExpect(model().attributeDoesNotExist("subjects"))
                .andExpect(model().attributeDoesNotExist("studentsForAdding"));
    }

    @Test
    @SneakyThrows
    @DisplayName("Should return status 302 and add subject to class")
    public void postAddSubjectToClassWithRoleAdminAndSubjectExist() {
        var payload = getDefaultClassDTO();
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();

        List<String> subjectsIdsAsString = payload.subjectsIds().stream()
                .map(String::valueOf)
                .toList();

        params.addAll("subjectsIds", subjectsIdsAsString);

        mockMvc.perform(MockMvcRequestBuilders.post(CLASSES_URL + "/1/subjects")
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .params(params)
                        .with(csrf()))
                .andExpect(status().is(HttpStatus.FOUND.value()));
    }

    @Test
    @SneakyThrows
    @DisplayName("Should return status 200 and page for editing existed class")
    public void getPageForEditingExistingClassForRoleAdmin() {
        when(classService.findById(any())).thenReturn(getDefaultClassDTO());

        mockMvc.perform(MockMvcRequestBuilders.get(CLASSES_URL + "/1/edit")
                        .accept(MediaType.TEXT_HTML).with(csrf()))
                .andExpect(status().is(HttpStatus.OK.value()))
                .andExpect(view().name("admin/classes/classes-edit"))
                .andExpect(model().attributeExists("cl"));
    }

    @Test
    @SneakyThrows
    @DisplayName("Should return status 200 and return error")
    public void getPageForEditingNotExistingClassForRoleAdmin() {
        when(classService.findById(any())).thenThrow(ClassEntityNotFoundException.class);
        when(messageSource.getMessage(any(), any(), any())).thenReturn("Класс c id=1 не найден");

        mockMvc.perform(MockMvcRequestBuilders.get(CLASSES_URL + "/1/edit")
                        .accept(MediaType.TEXT_HTML).with(csrf()))
                .andExpect(status().is(HttpStatus.OK.value()))
                .andExpect(view().name("error/error-app"))
                .andExpect(model().attributeExists("error"));
    }

    @Test
    @SneakyThrows
    @DisplayName("Should return status 403")
    public void getPageForAddingNewClassForRoleAdmin() {
        when(studentService.findStudentsWithNotClass()).thenReturn(List.of());
        when(subjectService.findSubjectsWithNotClass()).thenReturn(List.of());

        mockMvc.perform(MockMvcRequestBuilders.get(CLASSES_URL + "/add")
                        .accept(MediaType.TEXT_HTML).with(csrf()))
                .andExpect(status().is(HttpStatus.OK.value()))
                .andExpect(view().name("admin/classes/classes-add"))
                .andExpect(model().attributeExists("subjects"))
                .andExpect(model().attributeExists("students"));
    }

    @Test
    @SneakyThrows
    @WithMockUser(roles = "PARENT")
    @DisplayName("Should return status 403")
    public void getPageForAddingNewClassForRoleParent() {
        mockMvc.perform(MockMvcRequestBuilders.get(CLASSES_URL + "/add")
                        .accept(MediaType.TEXT_HTML).with(csrf()))
                .andExpect(status().is(HttpStatus.FORBIDDEN.value()));
    }

    @Test
    @SneakyThrows
    @WithMockUser(roles = "TEACHER")
    @DisplayName("Should return status 403")
    public void getPageForAddingNewClassForRoleTeacher() {
        mockMvc.perform(MockMvcRequestBuilders.get(CLASSES_URL + "/add")
                        .accept(MediaType.TEXT_HTML).with(csrf()))
                .andExpect(status().is(HttpStatus.FORBIDDEN.value()));
    }

    @Test
    @SneakyThrows
    @WithMockUser(roles = "STUDENT")
    @DisplayName("Should return status 403")
    public void getPageForAddingNewClassForRoleStudent() {
        mockMvc.perform(MockMvcRequestBuilders.get(CLASSES_URL + "/add")
                        .accept(MediaType.TEXT_HTML).with(csrf()))
                .andExpect(status().is(HttpStatus.FORBIDDEN.value()));
    }

    @Test
    @SneakyThrows
    @DisplayName("Should return status 302 and delete class")
    public void deleteExistedClassForRoleAdmin() {
        mockMvc.perform(MockMvcRequestBuilders.delete(CLASSES_URL + "/1")
                        .accept(MediaType.TEXT_HTML).with(csrf()))
                .andExpect(status().is(HttpStatus.FOUND.value()));
    }

    @Test
    @SneakyThrows
    @DisplayName("Should return status 200 and error")
    public void deleteNotExistedClassForRoleAdmin() {
        doThrow(ClassEntityNotFoundException.class).when(classService).deleteById(any());
        when(messageSource.getMessage(any(), any(), any())).thenReturn("Класс c id=1 не найден");

        mockMvc.perform(MockMvcRequestBuilders.delete(CLASSES_URL + "/1")
                        .accept(MediaType.TEXT_HTML).with(csrf()))
                .andExpect(status().is(HttpStatus.OK.value()))
                .andExpect(view().name("error/error-app"))
                .andExpect(model().attributeExists("error"));
    }

    @Test
    @SneakyThrows
    @WithMockUser(roles = "TEACHER")
    @DisplayName("Should return status 403")
    public void deleteClassForRoleTeacher() {
        mockMvc.perform(MockMvcRequestBuilders.get(CLASSES_URL + "/1")
                        .accept(MediaType.TEXT_HTML).with(csrf()))
                .andExpect(status().is(HttpStatus.FORBIDDEN.value()));
    }

    @Test
    @SneakyThrows
    @WithMockUser(roles = "PARENT")
    @DisplayName("Should return status 403")
    public void deleteClassForRoleParent() {
        mockMvc.perform(MockMvcRequestBuilders.get(CLASSES_URL + "/1")
                        .accept(MediaType.TEXT_HTML).with(csrf()))
                .andExpect(status().is(HttpStatus.FORBIDDEN.value()));
    }

    @Test
    @SneakyThrows
    @WithMockUser(roles = "STUDENT")
    @DisplayName("Should return status 403")
    public void deleteClassForRoleStudent() {
        mockMvc.perform(MockMvcRequestBuilders.get(CLASSES_URL + "/1")
                        .accept(MediaType.TEXT_HTML).with(csrf()))
                .andExpect(status().is(HttpStatus.FORBIDDEN.value()));
    }

    @Test
    @SneakyThrows
    @DisplayName("Should return status 302 and update class")
    public void putUpdateClassValidForRoleAdmin() {
        var payload = getDefaultClassDTO();

        putUpdateClassValid(payload);
    }

    @Test
    @SneakyThrows
    @DisplayName("Should return status 200 and error")
    public void putUpdateClassWithNotExistedIdValidForRoleAdmin() {
        var payload = getDefaultClassDTO();
        doThrow(ClassEntityNotFoundException.class).when(classService).update(any(), any());
        when(messageSource.getMessage(any(), any(), any())).thenReturn("Класс с id=1 не найден");

        mockMvc.perform(MockMvcRequestBuilders.put(CLASSES_URL + "/1")
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .param("letter", payload.letter())
                        .param("number", String.valueOf(payload.number()))
                        .param("description", payload.description())
                        .with(csrf()))
                .andExpect(status().is(HttpStatus.OK.value()))
                .andExpect(model().attributeExists("error"));
    }

    @Test
    @SneakyThrows
    @WithMockUser(roles = "TEACHER")
    @DisplayName("Should return status 403")
    public void putUpdateClassValidForRoleTeacher() {
        var payload = getDefaultClassDTO();

        mockMvc.perform(MockMvcRequestBuilders.put(CLASSES_URL + "/1")
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .param("letter", payload.letter())
                        .param("number", String.valueOf(payload.number()))
                        .param("description", payload.description())
                        .with(csrf()))
                .andExpect(status().is(HttpStatus.FORBIDDEN.value()));
    }

    @Test
    @SneakyThrows
    @WithMockUser(roles = "PARENT")
    @DisplayName("Should return status 403")
    public void putUpdateClassValidForRoleParent() {
        var payload = getDefaultClassDTO();

        mockMvc.perform(MockMvcRequestBuilders.put(CLASSES_URL + "/1")
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .param("letter", payload.letter())
                        .param("number", String.valueOf(payload.number()))
                        .param("description", payload.description())
                        .with(csrf()))
                .andExpect(status().is(HttpStatus.FORBIDDEN.value()));
    }

    @Test
    @SneakyThrows
    @WithMockUser(roles = "STUDENT")
    @DisplayName("Should return status 403")
    public void putUpdateClassValidForRoleStudent() {
        var payload = getDefaultClassDTO();

        mockMvc.perform(MockMvcRequestBuilders.put(CLASSES_URL + "/1")
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .param("letter", payload.letter())
                        .param("number", String.valueOf(payload.number()))
                        .param("description", payload.description())
                        .with(csrf()))
                .andExpect(status().is(HttpStatus.FORBIDDEN.value()));
    }



    @SneakyThrows
    @ParameterizedTest
    @DisplayName("Should return status 200 and errors")
    @MethodSource("provideInvalidClassDTOs")
    public void putUpdateClassInvalid(ClassDTO classDTO) {
        checkPostCreateInvalidClass(classDTO);
    }

    @Test
    @SneakyThrows
    @DisplayName("Should return status 302 and create new class")
    public void putCreateClassValidForRoleAdmin() {
        var payload = getDefaultClassDTO();

        mockMvc.perform(MockMvcRequestBuilders.post(CLASSES_URL)
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .param("letter", payload.letter())
                        .param("number", String.valueOf(payload.number()))
                        .param("description", payload.description())
                        .with(csrf()))
                .andExpect(status().is(HttpStatus.FOUND.value()));
    }

    @Test
    @SneakyThrows
    @WithMockUser(roles = "STUDENT")
    @DisplayName("Should return status 403")
    public void postCreateClassValidForRoleStudent() {
        var payload = getDefaultClassDTO();

        mockMvc.perform(MockMvcRequestBuilders.post(CLASSES_URL)
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .param("letter", payload.letter())
                        .param("number", String.valueOf(payload.number()))
                        .param("description", payload.description())
                        .with(csrf()))
                .andExpect(status().is(HttpStatus.FORBIDDEN.value()));
    }

    @Test
    @SneakyThrows
    @WithMockUser(roles = "PARENT")
    @DisplayName("Should return status 403")
    public void postCreateClassValidForRoleParent() {
        var payload = getDefaultClassDTO();

        mockMvc.perform(MockMvcRequestBuilders.post(CLASSES_URL)
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .param("letter", payload.letter())
                        .param("number", String.valueOf(payload.number()))
                        .param("description", payload.description())
                        .with(csrf()))
                .andExpect(status().is(HttpStatus.FORBIDDEN.value()));
    }

    @Test
    @SneakyThrows
    @WithMockUser(roles = "TEACHER")
    @DisplayName("Should return status 403")
    public void postCreateClassValidForRoleTeacher() {
        var payload = getDefaultClassDTO();

        mockMvc.perform(MockMvcRequestBuilders.post(CLASSES_URL)
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .param("letter", payload.letter())
                        .param("number", String.valueOf(payload.number()))
                        .param("description", payload.description())
                        .with(csrf()))
                .andExpect(status().is(HttpStatus.FORBIDDEN.value()));
    }

    @SneakyThrows
    @ParameterizedTest
    @DisplayName("Should return status 200 and errors")
    @MethodSource("provideInvalidClassDTOs")
    public void postCreateClassInvalid(ClassDTO classDTO) {
        checkPutUpdateInvalidClass(classDTO);
    }

    @SneakyThrows
    private void checkPostCreateInvalidClass(ClassDTO classDTO) {
        mockMvc.perform(MockMvcRequestBuilders.post(CLASSES_URL)
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .param("letter", classDTO.letter())
                        .param("number", String.valueOf(classDTO.number()))
                        .param("description", classDTO.description())
                        .with(csrf()))
                .andExpect(status().is(HttpStatus.OK.value()))
                .andExpect(model().attributeExists("errors"));
    }

    @SneakyThrows
    private void checkPutUpdateInvalidClass(ClassDTO classDTO) {
        mockMvc.perform(MockMvcRequestBuilders.put(CLASSES_URL+"/1")
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .param("letter", classDTO.letter())
                        .param("number", String.valueOf(classDTO.number()))
                        .param("description", classDTO.description())
                        .with(csrf()))
                .andExpect(status().is(HttpStatus.OK.value()))
                .andExpect(model().attributeExists("errors"));
    }

    @SneakyThrows
    private void putUpdateClassValid(ClassDTO classDTO) {
        mockMvc.perform(MockMvcRequestBuilders.put(CLASSES_URL + "/1")
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .param("letter", classDTO.letter())
                        .param("number", String.valueOf(classDTO.number()))
                        .param("description", classDTO.description())
                        .with(csrf()))
                .andExpect(status().is(HttpStatus.FOUND.value()));
    }
}
