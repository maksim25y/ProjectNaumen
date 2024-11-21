package ru.mudan.controllers;

import java.util.ArrayList;
import java.util.function.Supplier;
import java.util.stream.Stream;
import lombok.SneakyThrows;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import ru.mudan.dto.auth.RegisterUserDTO;
import ru.mudan.services.students.StudentService;
import ru.mudan.services.users.RegistrationService;

import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static ru.mudan.UtilConstants.*;

@WithMockUser(roles = "ADMIN")
public class AuthControllerTest extends BaseControllerTest {

    private static final Supplier<Stream<Arguments>> invalidRegisterUserDTOs = () -> Stream.of(
            Arguments.of(new RegisterUserDTO("И", "Иванов", "Иванович", "ivan@mail.ru", "testPassword", null)),
            Arguments.of(new RegisterUserDTO("Иваниваниваниванивани", "Иванов", "Иванович", "ivan@mail.ru", "testPassword", null)),
            Arguments.of(new RegisterUserDTO("иван", "Иванов", "Иванович", "ivan@mail.ru", "testPassword", null)),
            Arguments.of(new RegisterUserDTO("Ivan", "Иванов", "Иванович", "ivan@mail.ru", "testPassword", null)),
            Arguments.of(new RegisterUserDTO("Иван", "И", "Иванович", "ivan@mail.ru", "testPassword", null)),
            Arguments.of(new RegisterUserDTO("Иван", "Иваниваниваниванивани", "Иванович", "ivan@mail.ru", "testPassword", null)),
            Arguments.of(new RegisterUserDTO("Иван", "иванов", "Иванович", "ivan@mail.ru", "testPassword", null)),
            Arguments.of(new RegisterUserDTO("Иван", "Ivanov", "Иванович", "ivan@mail.ru", "testPassword", null)),
            Arguments.of(new RegisterUserDTO("Иван", "Иванов", "Ив", "ivan@mail.ru", "testPassword", null)),
            Arguments.of(new RegisterUserDTO("Иван", "Иванов", "Иваниваниваниванивани", "ivan@mail.ru", "testPassword", null)),
            Arguments.of(new RegisterUserDTO("Иван", "Иванов", "иванович", "ivan@mail.ru", "testPassword", null)),
            Arguments.of(new RegisterUserDTO("Иван", "Иванов", "Ivanovich", "ivan@mail.ru", "testPassword", null)),
            Arguments.of(new RegisterUserDTO(null, "Иванов", "Иванович", "ivan@mail.ru", "testPassword", null)),
            Arguments.of(new RegisterUserDTO("", "Иванов", "Иванович", "ivan@mail.ru", "testPassword", null)),
            Arguments.of(new RegisterUserDTO("Иван", null, "Иванович", "ivan@mail.ru", "testPassword", null)),
            Arguments.of(new RegisterUserDTO("Иван", "", "Иванович", "ivan@mail.ru", "testPassword", null)),
            Arguments.of(new RegisterUserDTO("Иван", "Иванов", null, "ivan@mail.ru", "testPassword", null)),
            Arguments.of(new RegisterUserDTO("Иван", "Иванов", "", "ivan@mail.ru", "testPassword", null)),
            Arguments.of(new RegisterUserDTO("Иван", "Иванов", "Иванович", "ivan@mail.ru", "tes", null)),
            Arguments.of(new RegisterUserDTO("Иван", "Иванов", "Иванович", "ivan@mail.ru", "testPassword111111111", null)),
            Arguments.of(new RegisterUserDTO("Иван", "Иванов", "Иванович", "ivan@mail.ru", null, null)),
            Arguments.of(new RegisterUserDTO("Иван", "Иванов", "Иванович", "ivan@mail.ru", "", null)),
            Arguments.of(new RegisterUserDTO("Иван", "Иванов", "Иванович", "ivan", "testPassword", null)),
            Arguments.of(new RegisterUserDTO("Иван", "Иванов", "Иванович", "", "testPassword", null)),
            Arguments.of(new RegisterUserDTO("Иван", "Иванов", "Иванович", null, "testPassword", null))
    );

    public static Stream<Arguments> provideInvalidRegisterUserDTOs() {
        return invalidRegisterUserDTOs.get();
    }

    private final String REGISTRATION = "registration";

    @MockBean
    private RegistrationService registrationService;
    @MockBean
    private StudentService studentService;

    @SneakyThrows
    @ParameterizedTest
    @MethodSource("provideInvalidRegisterUserDTOs")
    public void postRegisterNewTeacher_invalid(RegisterUserDTO registerUserDTO) {
        checkPostRegisteringInvalid("/teacher", registerUserDTO);
    }

    @SneakyThrows
    @ParameterizedTest
    @MethodSource("provideInvalidRegisterUserDTOs")
    public void postRegisterNewParent_invalid(RegisterUserDTO registerUserDTO) {
        checkPostRegisteringInvalid("/parent", registerUserDTO);
    }

    @SneakyThrows
    @ParameterizedTest
    @MethodSource("provideInvalidRegisterUserDTOs")
    public void postRegisterNewStudent_invalid(RegisterUserDTO registerUserDTO) {
        checkPostRegisteringInvalid("/student", registerUserDTO);
    }

    @SneakyThrows
    @ParameterizedTest
    @MethodSource("provideInvalidRegisterUserDTOs")
    public void postRegisterNewAdmin_invalid(RegisterUserDTO registerUserDTO) {
        checkPostRegisteringInvalid("/admin", registerUserDTO);
    }

    @SneakyThrows
    @Test
    @DisplayName("Should register new admin and return status 200")
    public void postMethodRegisterNewAdmin_roleAdmin() {
        postRegisterUserValid("/admin", HttpStatus.FOUND);
    }

    @SneakyThrows
    @Test
    @DisplayName("Should return status 403")
    @WithMockUser(roles = "PARENT")
    public void postMethodForRegisterNewAdmin_roleParent() {
        postRegisterUserValid("/admin", HttpStatus.FORBIDDEN);
    }

    @SneakyThrows
    @Test
    @DisplayName("Should return status 403")
    @WithMockUser(roles = "TEACHER")
    public void postMethodForRegisterNewAdmin_roleTeacher() {
        postRegisterUserValid("/admin", HttpStatus.FORBIDDEN);
    }

    @SneakyThrows
    @Test
    @DisplayName("Should return status 403")
    @WithMockUser(roles = "STUDENT")
    public void postMethodForRegisterNewAdmin_roleStudent() {
        postRegisterUserValid("/admin", HttpStatus.FORBIDDEN);
    }

    @SneakyThrows
    @Test
    @DisplayName("Should register new teacher and return status 200")
    public void postMethodForRegisterNewTeacher_roleAdmin() {
        postRegisterUserValid("/teacher", HttpStatus.FOUND);
    }

    @SneakyThrows
    @Test
    @DisplayName("Should return status 403")
    @WithMockUser(roles = "PARENT")
    public void postMethodForRegisterNewTeacher_roleParent() {
        postRegisterUserValid("/teacher", HttpStatus.FORBIDDEN);
    }

    @SneakyThrows
    @Test
    @DisplayName("Should return status 403")
    @WithMockUser(roles = "TEACHER")
    public void postMethodForRegisterNewTeacher_roleTeacher() {
        postRegisterUserValid("/teacher", HttpStatus.FORBIDDEN);
    }

    @SneakyThrows
    @Test
    @DisplayName("Should return status 403")
    @WithMockUser(roles = "STUDENT")
    public void postMethodForRegisterNewTeacher_roleStudent() {
        postRegisterUserValid("/teacher", HttpStatus.FORBIDDEN);
    }

    @SneakyThrows
    @Test
    @DisplayName("Should register new student and return status 200")
    public void postMethodForRegisterNewStudent_roleAdmin() {
        postRegisterUserValid("/student", HttpStatus.FOUND);
    }

    @SneakyThrows
    @Test
    @DisplayName("Should return status 403")
    @WithMockUser(roles = "PARENT")
    public void postMethodForRegisterNewStudent_roleParent() {
        postRegisterUserValid("/student", HttpStatus.FORBIDDEN);
    }

    @SneakyThrows
    @Test
    @DisplayName("Should return status 403")
    @WithMockUser(roles = "TEACHER")
    public void postMethodForRegisterNewStudent_roleTeacher() {
        postRegisterUserValid("/student", HttpStatus.FORBIDDEN);
    }

    @SneakyThrows
    @Test
    @DisplayName("Should return status 403")
    @WithMockUser(roles = "STUDENT")
    public void postMethodForRegisterNewStudent_roleStudent() {
        postRegisterUserValid("/student", HttpStatus.FORBIDDEN);
    }

    @SneakyThrows
    @Test
    @DisplayName("Should register new parent and return status 200")
    public void postMethodForRegisterNewParent_roleAdmin() {
        postRegisterUserValid("/parent", HttpStatus.FOUND);
    }

    @SneakyThrows
    @Test
    @DisplayName("Should return status 403")
    @WithMockUser(roles = "PARENT")
    public void postMethodForRegisterNewParent_roleParent() {
        postRegisterUserValid("/parent", HttpStatus.FORBIDDEN);
    }

    @SneakyThrows
    @Test
    @DisplayName("Should return status 403")
    @WithMockUser(roles = "TEACHER")
    public void postMethodForRegisterNewParent_roleTeacher() {
        postRegisterUserValid("/parent", HttpStatus.FORBIDDEN);
    }

    @SneakyThrows
    @Test
    @DisplayName("Should return status 403")
    @WithMockUser(roles = "STUDENT")
    public void postMethodForRegisterNewParent_roleStudent() {
        postRegisterUserValid("/parent", HttpStatus.FORBIDDEN);
    }

    @SneakyThrows
    @Test
    @DisplayName("Should return page for registering admin")
    public void getMethodForRegisterNewAdminForUser_roleAdmin() {
        mockMvc.perform(MockMvcRequestBuilders.get(AUTH_URL+"/admin")
                        .accept(MediaType.TEXT_HTML).with(csrf()))
                .andExpect(status().isOk())
                .andExpect(view().name(REGISTRATION+"/registration-admin"));
    }

    @SneakyThrows
    @Test
    @DisplayName("Should return status 403")
    @WithMockUser(roles = "STUDENT")
    public void getMethodForRegisterNewAdminForUser_roleStudent() {
        mockMvc.perform(MockMvcRequestBuilders.get(AUTH_URL+"/admin")
                        .accept(MediaType.TEXT_HTML).with(csrf()))
                .andExpect(status().is(HttpStatus.FORBIDDEN.value()));
    }

    @SneakyThrows
    @Test
    @DisplayName("Should return status 403")
    @WithMockUser(roles = "PARENT")
    public void getMethodForRegisterNewAdminForUser_roleParent() {
        mockMvc.perform(MockMvcRequestBuilders.get(AUTH_URL+"/admin")
                        .accept(MediaType.TEXT_HTML).with(csrf()))
                .andExpect(status().is(HttpStatus.FORBIDDEN.value()));
    }

    @SneakyThrows
    @Test
    @DisplayName("Should return status 403")
    @WithMockUser(roles = "TEACHER")
    public void getMethodForRegisterNewAdminForUser_roleTeacher() {
        mockMvc.perform(MockMvcRequestBuilders.get(AUTH_URL+"/admin")
                        .accept(MediaType.TEXT_HTML).with(csrf()))
                .andExpect(status().is(HttpStatus.FORBIDDEN.value()));
    }

    @SneakyThrows
    @Test
    @DisplayName("Should return page for registering student")
    public void getMethodForRegisterNewStudentForUser_roleAdmin() {
        mockMvc.perform(MockMvcRequestBuilders.get(AUTH_URL+"/student")
                        .accept(MediaType.TEXT_HTML).with(csrf()))
                .andExpect(status().is(HttpStatus.OK.value()))
                .andExpect(view().name(REGISTRATION+"/registration-student"));
    }

    @SneakyThrows
    @Test
    @DisplayName("Should return status 403")
    @WithMockUser(roles = "STUDENT")
    public void getMethodForRegisterNewStudentForUser_roleStudent() {
        mockMvc.perform(MockMvcRequestBuilders.get(AUTH_URL+"/student")
                        .accept(MediaType.TEXT_HTML).with(csrf()))
                .andExpect(status().is(HttpStatus.FORBIDDEN.value()));
    }

    @SneakyThrows
    @Test
    @DisplayName("Should return status 403")
    @WithMockUser(roles = "PARENT")
    public void getMethodForRegisterNewStudentForUser_roleParent() {
        mockMvc.perform(MockMvcRequestBuilders.get(AUTH_URL+"/student")
                        .accept(MediaType.TEXT_HTML).with(csrf()))
                .andExpect(status().is(HttpStatus.FORBIDDEN.value()));
    }

    @SneakyThrows
    @Test
    @DisplayName("Should return status 403")
    @WithMockUser(roles = "TEACHER")
    public void getMethodForRegisterNewStudentForUser_roleTeacher() {
        mockMvc.perform(MockMvcRequestBuilders.get(AUTH_URL+"/student")
                        .accept(MediaType.TEXT_HTML).with(csrf()))
                .andExpect(status().is(HttpStatus.FORBIDDEN.value()));
    }

    @SneakyThrows
    @Test
    @DisplayName("Should return page for registering student")
    public void getMethodForRegisterNewTeacherForUser_roleAdmin() {
        mockMvc.perform(MockMvcRequestBuilders.get(AUTH_URL+"/teacher")
                        .accept(MediaType.TEXT_HTML).with(csrf()))
                .andExpect(status().is(HttpStatus.OK.value()))
                .andExpect(view().name(REGISTRATION+"/registration-teacher"));
    }

    @SneakyThrows
    @Test
    @DisplayName("Should return status 403")
    @WithMockUser(roles = "STUDENT")
    public void getMethodForRegisterNewTeacherForUser_roleStudent() {
        mockMvc.perform(MockMvcRequestBuilders.get(AUTH_URL+"/teacher")
                        .accept(MediaType.TEXT_HTML).with(csrf()))
                .andExpect(status().is(HttpStatus.FORBIDDEN.value()));
    }

    @SneakyThrows
    @Test
    @DisplayName("Should return status 403")
    @WithMockUser(roles = "PARENT")
    public void getMethodForRegisterNewTeacherForUser_roleParent() {
        mockMvc.perform(MockMvcRequestBuilders.get(AUTH_URL+"/teacher")
                        .accept(MediaType.TEXT_HTML).with(csrf()))
                .andExpect(status().is(HttpStatus.FORBIDDEN.value()));
    }

    @SneakyThrows
    @Test
    @DisplayName("Should return status 403")
    @WithMockUser(roles = "TEACHER")
    public void getMethodForRegisterNewTeacherForUser_roleTeacher() {
        mockMvc.perform(MockMvcRequestBuilders.get(AUTH_URL+"/teacher")
                        .accept(MediaType.TEXT_HTML).with(csrf()))
                .andExpect(status().is(HttpStatus.FORBIDDEN.value()));
    }

    @SneakyThrows
    @Test
    @DisplayName("Should return page for registering student")
    public void getMethodForRegisterNewParentForUser_roleAdmin() {
        when(studentService.findAllStudentsWithNotParent()).thenReturn(new ArrayList<>());

        mockMvc.perform(MockMvcRequestBuilders.get(AUTH_URL+"/parent")
                        .accept(MediaType.TEXT_HTML).with(csrf()))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("students"))
                .andExpect(view().name(REGISTRATION+"/registration-parent"));
    }

    @SneakyThrows
    @Test
    @DisplayName("Should return status 403")
    @WithMockUser(roles = "STUDENT")
    public void getMethodForRegisterNewParentForUser_roleStudent() {
        checkForbiddenGetRegistering();
    }

    @SneakyThrows
    @Test
    @DisplayName("Should return status 403")
    @WithMockUser(roles = "PARENT")
    public void getMethodForRegisterNewParentForUser_roleParent() {
        checkForbiddenGetRegistering();
    }

    @SneakyThrows
    @Test
    @DisplayName("Should return status 403")
    @WithMockUser(roles = "TEACHER")
    public void getMethodForRegisterNewParentForUser_roleTeacher() {
        checkForbiddenGetRegistering();
    }

    private void checkForbiddenGetRegistering() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get(AUTH_URL+"/parent")
                        .accept(MediaType.TEXT_HTML).with(csrf()))
                .andExpect(status().is(HttpStatus.FORBIDDEN.value()));
    }

    @SneakyThrows
    private void checkPostRegisteringInvalid(String path, RegisterUserDTO registerUserDTO) {
        mockMvc.perform(MockMvcRequestBuilders.post(AUTH_URL + path)
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .param("lastname", registerUserDTO.lastname())
                        .param("firstname", registerUserDTO.firstname())
                        .param("patronymic", registerUserDTO.patronymic())
                        .param("email", registerUserDTO.email())
                        .param("password", registerUserDTO.password())
                        .with(csrf()))
                .andExpect(status().is(HttpStatus.OK.value()))
                .andExpect(model().attributeExists("errors"));
    }

    @SneakyThrows
    private void postRegisterUserValid(String path, HttpStatus forbidden) {
        var payload = getDefaultRegisterUserDTO();

        mockMvc.perform(MockMvcRequestBuilders.post(AUTH_URL + path)
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .param("lastname", payload.lastname())
                        .param("firstname", payload.firstname())
                        .param("patronymic", payload.patronymic())
                        .param("email", payload.email())
                        .param("password", payload.password())
                        .with(csrf()))
                .andExpect(status().is(forbidden.value()));
    }

}
