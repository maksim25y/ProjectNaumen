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
            Arguments.of(new RegisterUserDTO("Ива", "Иванов", "Иванович", "ivan@mail.ru", "testPassword", null)),
            Arguments.of(new RegisterUserDTO("Иваниваниваниванивани", "Иванов", "Иванович", "ivan@mail.ru", "testPassword", null)),
            Arguments.of(new RegisterUserDTO("иван", "Иванов", "Иванович", "ivan@mail.ru", "testPassword", null)),
            Arguments.of(new RegisterUserDTO("Ivan", "Иванов", "Иванович", "ivan@mail.ru", "testPassword", null)),
            Arguments.of(new RegisterUserDTO("Иван", "Ива", "Иванович", "ivan@mail.ru", "testPassword", null)),
            Arguments.of(new RegisterUserDTO("Иван", "Иваниваниваниванивани", "Иванович", "ivan@mail.ru", "testPassword", null)),
            Arguments.of(new RegisterUserDTO("Иван", "иванов", "Иванович", "ivan@mail.ru", "testPassword", null)),
            Arguments.of(new RegisterUserDTO("Иван", "Ivanov", "Иванович", "ivan@mail.ru", "testPassword", null)),
            Arguments.of(new RegisterUserDTO("Иван", "Иванов", "Ива", "ivan@mail.ru", "testPassword", null)),
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
    public void postRegisterNewTeacherInvalid(RegisterUserDTO registerUserDTO) {
        checkPostRegisteringInvalid("/teacher", registerUserDTO);
    }

    @SneakyThrows
    @ParameterizedTest
    @MethodSource("provideInvalidRegisterUserDTOs")
    public void postRegisterNewParentInvalid(RegisterUserDTO registerUserDTO) {
        checkPostRegisteringInvalid("/parent", registerUserDTO);
    }

    @SneakyThrows
    @ParameterizedTest
    @MethodSource("provideInvalidRegisterUserDTOs")
    public void postRegisterNewStudentInvalid(RegisterUserDTO registerUserDTO) {
        checkPostRegisteringInvalid("/student", registerUserDTO);
    }

    @SneakyThrows
    @ParameterizedTest
    @MethodSource("provideInvalidRegisterUserDTOs")
    public void postRegisterNewAdminInvalid(RegisterUserDTO registerUserDTO) {
        checkPostRegisteringInvalid("/admin", registerUserDTO);
    }

    @SneakyThrows
    @Test
    @DisplayName("Should register new admin and return status 200")
    public void postMethodForRegisterNewAdminWithRoleAdmin() {
        postRegisterUserValid("/admin", HttpStatus.FOUND);
    }

    @SneakyThrows
    @Test
    @DisplayName("Should return status 403")
    @WithMockUser(roles = "PARENT")
    public void postMethodForRegisterNewAdminWithRoleParent() {
        postRegisterUserValid("/admin", HttpStatus.FORBIDDEN);
    }

    @SneakyThrows
    @Test
    @DisplayName("Should return status 403")
    @WithMockUser(roles = "TEACHER")
    public void postMethodForRegisterNewAdminWithRoleTeacher() {
        postRegisterUserValid("/admin", HttpStatus.FORBIDDEN);
    }

    @SneakyThrows
    @Test
    @DisplayName("Should return status 403")
    @WithMockUser(roles = "STUDENT")
    public void postMethodForRegisterNewAdminWithRoleStudent() {
        postRegisterUserValid("/admin", HttpStatus.FORBIDDEN);
    }

    @SneakyThrows
    @Test
    @DisplayName("Should register new teacher and return status 200")
    public void postMethodForRegisterNewTeacherWithRoleAdmin() {
        postRegisterUserValid("/teacher", HttpStatus.FOUND);
    }

    @SneakyThrows
    @Test
    @DisplayName("Should return status 403")
    @WithMockUser(roles = "PARENT")
    public void postMethodForRegisterNewTeacherWithRoleParent() {
        postRegisterUserValid("/teacher", HttpStatus.FORBIDDEN);
    }

    @SneakyThrows
    @Test
    @DisplayName("Should return status 403")
    @WithMockUser(roles = "TEACHER")
    public void postMethodForRegisterNewTeacherWithRoleTeacher() {
        postRegisterUserValid("/teacher", HttpStatus.FORBIDDEN);
    }

    @SneakyThrows
    @Test
    @DisplayName("Should return status 403")
    @WithMockUser(roles = "STUDENT")
    public void postMethodForRegisterNewTeacherWithRoleStudent() {
        postRegisterUserValid("/teacher", HttpStatus.FORBIDDEN);
    }

    @SneakyThrows
    @Test
    @DisplayName("Should register new student and return status 200")
    public void postMethodForRegisterNewStudentWithRoleAdmin() {
        postRegisterUserValid("/student", HttpStatus.FOUND);
    }

    @SneakyThrows
    @Test
    @DisplayName("Should return status 403")
    @WithMockUser(roles = "PARENT")
    public void postMethodForRegisterNewStudentWithRoleParent() {
        postRegisterUserValid("/student", HttpStatus.FORBIDDEN);
    }

    @SneakyThrows
    @Test
    @DisplayName("Should return status 403")
    @WithMockUser(roles = "TEACHER")
    public void postMethodForRegisterNewStudentWithRoleTeacher() {
        postRegisterUserValid("/student", HttpStatus.FORBIDDEN);
    }

    @SneakyThrows
    @Test
    @DisplayName("Should return status 403")
    @WithMockUser(roles = "STUDENT")
    public void postMethodForRegisterNewStudentWithRoleStudent() {
        postRegisterUserValid("/student", HttpStatus.FORBIDDEN);
    }

    @SneakyThrows
    @Test
    @DisplayName("Should register new parent and return status 200")
    public void postMethodForRegisterNewParentWithRoleAdmin() {
        postRegisterUserValid("/parent", HttpStatus.FOUND);
    }

    @SneakyThrows
    @Test
    @DisplayName("Should return status 403")
    @WithMockUser(roles = "PARENT")
    public void postMethodForRegisterNewParentWithRoleParent() {
        postRegisterUserValid("/parent", HttpStatus.FORBIDDEN);
    }

    @SneakyThrows
    @Test
    @DisplayName("Should return status 403")
    @WithMockUser(roles = "TEACHER")
    public void postMethodForRegisterNewParentWithRoleTeacher() {
        postRegisterUserValid("/parent", HttpStatus.FORBIDDEN);
    }

    @SneakyThrows
    @Test
    @DisplayName("Should return status 403")
    @WithMockUser(roles = "STUDENT")
    public void postMethodForRegisterNewParentWithRoleStudent() {
        postRegisterUserValid("/parent", HttpStatus.FORBIDDEN);
    }

    @SneakyThrows
    @Test
    @DisplayName("Should return page for registering admin")
    public void getMethodForRegisterNewAdminForUserWithRoleAdmin() {
        mockMvc.perform(MockMvcRequestBuilders.get(AUTH_URL+"/admin")
                        .accept(MediaType.TEXT_HTML).with(csrf()))
                .andExpect(status().isOk())
                .andExpect(view().name(REGISTRATION+"/registration-admin"));
    }

    @SneakyThrows
    @Test
    @DisplayName("Should return status 403")
    @WithMockUser(roles = "STUDENT")
    public void getMethodForRegisterNewAdminForUserWithRoleStudent() {
        mockMvc.perform(MockMvcRequestBuilders.get(AUTH_URL+"/admin")
                        .accept(MediaType.TEXT_HTML).with(csrf()))
                .andExpect(status().is(HttpStatus.FORBIDDEN.value()));
    }

    @SneakyThrows
    @Test
    @DisplayName("Should return status 403")
    @WithMockUser(roles = "PARENT")
    public void getMethodForRegisterNewAdminForUserWithRoleParent() {
        mockMvc.perform(MockMvcRequestBuilders.get(AUTH_URL+"/admin")
                        .accept(MediaType.TEXT_HTML).with(csrf()))
                .andExpect(status().is(HttpStatus.FORBIDDEN.value()));
    }

    @SneakyThrows
    @Test
    @DisplayName("Should return status 403")
    @WithMockUser(roles = "TEACHER")
    public void getMethodForRegisterNewAdminForUserWithRoleTeacher() {
        mockMvc.perform(MockMvcRequestBuilders.get(AUTH_URL+"/admin")
                        .accept(MediaType.TEXT_HTML).with(csrf()))
                .andExpect(status().is(HttpStatus.FORBIDDEN.value()));
    }

    @SneakyThrows
    @Test
    @DisplayName("Should return page for registering student")
    public void getMethodForRegisterNewStudentForUserWithRoleAdmin() {
        mockMvc.perform(MockMvcRequestBuilders.get(AUTH_URL+"/student")
                        .accept(MediaType.TEXT_HTML).with(csrf()))
                .andExpect(status().is(HttpStatus.OK.value()))
                .andExpect(view().name(REGISTRATION+"/registration-student"));
    }

    @SneakyThrows
    @Test
    @DisplayName("Should return status 403")
    @WithMockUser(roles = "STUDENT")
    public void getMethodForRegisterNewStudentForUserWithRoleStudent() {
        mockMvc.perform(MockMvcRequestBuilders.get(AUTH_URL+"/student")
                        .accept(MediaType.TEXT_HTML).with(csrf()))
                .andExpect(status().is(HttpStatus.FORBIDDEN.value()));
    }

    @SneakyThrows
    @Test
    @DisplayName("Should return status 403")
    @WithMockUser(roles = "PARENT")
    public void getMethodForRegisterNewStudentForUserWithRoleParent() {
        mockMvc.perform(MockMvcRequestBuilders.get(AUTH_URL+"/student")
                        .accept(MediaType.TEXT_HTML).with(csrf()))
                .andExpect(status().is(HttpStatus.FORBIDDEN.value()));
    }

    @SneakyThrows
    @Test
    @DisplayName("Should return status 403")
    @WithMockUser(roles = "TEACHER")
    public void getMethodForRegisterNewStudentForUserWithRoleTeacher() {
        mockMvc.perform(MockMvcRequestBuilders.get(AUTH_URL+"/student")
                        .accept(MediaType.TEXT_HTML).with(csrf()))
                .andExpect(status().is(HttpStatus.FORBIDDEN.value()));
    }

    @SneakyThrows
    @Test
    @DisplayName("Should return page for registering student")
    public void getMethodForRegisterNewTeacherForUserWithRoleAdmin() {
        mockMvc.perform(MockMvcRequestBuilders.get(AUTH_URL+"/teacher")
                        .accept(MediaType.TEXT_HTML).with(csrf()))
                .andExpect(status().is(HttpStatus.OK.value()))
                .andExpect(view().name(REGISTRATION+"/registration-teacher"));
    }

    @SneakyThrows
    @Test
    @DisplayName("Should return status 403")
    @WithMockUser(roles = "STUDENT")
    public void getMethodForRegisterNewTeacherForUserWithRoleStudent() {
        mockMvc.perform(MockMvcRequestBuilders.get(AUTH_URL+"/teacher")
                        .accept(MediaType.TEXT_HTML).with(csrf()))
                .andExpect(status().is(HttpStatus.FORBIDDEN.value()));
    }

    @SneakyThrows
    @Test
    @DisplayName("Should return status 403")
    @WithMockUser(roles = "PARENT")
    public void getMethodForRegisterNewTeacherForUserWithRoleParent() {
        mockMvc.perform(MockMvcRequestBuilders.get(AUTH_URL+"/teacher")
                        .accept(MediaType.TEXT_HTML).with(csrf()))
                .andExpect(status().is(HttpStatus.FORBIDDEN.value()));
    }

    @SneakyThrows
    @Test
    @DisplayName("Should return status 403")
    @WithMockUser(roles = "TEACHER")
    public void getMethodForRegisterNewTeacherForUserWithRoleTeacher() {
        mockMvc.perform(MockMvcRequestBuilders.get(AUTH_URL+"/teacher")
                        .accept(MediaType.TEXT_HTML).with(csrf()))
                .andExpect(status().is(HttpStatus.FORBIDDEN.value()));
    }

    @SneakyThrows
    @Test
    @DisplayName("Should return page for registering student")
    public void getMethodForRegisterNewParentForUserWithRoleAdmin() {
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
    public void getMethodForRegisterNewParentForUserWithRoleStudent() {
        checkForbiddenGetRegistering();
    }

    @SneakyThrows
    @Test
    @DisplayName("Should return status 403")
    @WithMockUser(roles = "PARENT")
    public void getMethodForRegisterNewParentForUserWithRoleParent() {
        checkForbiddenGetRegistering();
    }

    @SneakyThrows
    @Test
    @DisplayName("Should return status 403")
    @WithMockUser(roles = "TEACHER")
    public void getMethodForRegisterNewParentForUserWithRoleTeacher() {
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
