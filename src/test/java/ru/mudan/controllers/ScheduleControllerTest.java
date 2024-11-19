package ru.mudan.controllers;

import java.time.LocalTime;
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
import ru.mudan.dto.schedule.ScheduleCreateDTO;
import ru.mudan.dto.schedule.ScheduleUpdateDTO;
import ru.mudan.exceptions.base.ApplicationForbiddenException;
import ru.mudan.exceptions.entity.not_found.ClassEntityNotFoundException;
import ru.mudan.exceptions.entity.not_found.ScheduleNotFoundException;
import ru.mudan.services.auth.AuthService;
import ru.mudan.services.schedule.ScheduleService;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static ru.mudan.UtilConstants.*;

@WithMockUser(roles = "ADMIN")
public class ScheduleControllerTest extends BaseControllerTest {
    private static final Supplier<Stream<Arguments>> invalidScheduleCreateDTOs = () -> Stream.of(
            Arguments.of(new ScheduleCreateDTO(0, LocalTime.now(), 1, 1L, 1L)),
            Arguments.of(new ScheduleCreateDTO(8, LocalTime.now(), 1, 1L, 1L)),
            Arguments.of(new ScheduleCreateDTO(1, null, 1, 1L, 1L)),
            Arguments.of(new ScheduleCreateDTO(1, LocalTime.now(), 0, 1L, 1L)),
            Arguments.of(new ScheduleCreateDTO(1, LocalTime.now(), 301, 1L, 1L)),
            Arguments.of(new ScheduleCreateDTO(1, LocalTime.now(), 1, null, 1L)),
            Arguments.of(new ScheduleCreateDTO(1, LocalTime.now(), 1, 1L, null))
            );

    private static final Supplier<Stream<Arguments>> invalidScheduleUpdateDTOs = () -> Stream.of(
            Arguments.of(new ScheduleUpdateDTO(0, LocalTime.now(), 1)),
            Arguments.of(new ScheduleUpdateDTO(8, LocalTime.now(), 1)),
            Arguments.of(new ScheduleUpdateDTO(1, null, 1)),
            Arguments.of(new ScheduleUpdateDTO(1, LocalTime.now(), 0)),
            Arguments.of(new ScheduleUpdateDTO(1, LocalTime.now(), 301))
            );

    public static Stream<Arguments> provideInvalidScheduleCreateDTOs() {
        return invalidScheduleCreateDTOs.get();
    }

    public static Stream<Arguments> provideInvalidScheduleUpdateDTOs() {
        return invalidScheduleUpdateDTOs.get();
    }

    @MockBean
    private ScheduleService scheduleService;
    @MockBean
    private AuthService authService;
    @MockBean
    private MessageSource messageSource;

    @Test
    @SneakyThrows
    public void getAllSchedulesForExistedClassForRoleAdmin() {
        when(scheduleService.findAllSchedulesForClass(any())).thenReturn(List.of(getDefaultScheduleDTO()));

        mockMvc.perform(MockMvcRequestBuilders.get(SCHEDULES_URL + "/all/1")
                        .accept(MediaType.TEXT_HTML).with(csrf()))
                .andExpect(status().is(HttpStatus.OK.value()))
                .andExpect(model().attributeExists("schedules"));
    }

    @Test
    @SneakyThrows
    public void getAllSchedulesForNotExistedClassForRoleAdmin() {
        when(scheduleService.findAllSchedulesForClass(any())).thenThrow(ClassEntityNotFoundException.class);
        when(messageSource.getMessage(any(), any(), any())).thenReturn("Класс с id=1 не найден");

        mockMvc.perform(MockMvcRequestBuilders.get(SCHEDULES_URL + "/all/1")
                        .accept(MediaType.TEXT_HTML).with(csrf()))
                .andExpect(status().is(HttpStatus.OK.value()))
                .andExpect(view().name("error/error-app"))
                .andExpect(model().attributeExists("error"));
    }

    @Test
    @SneakyThrows
    @WithMockUser(roles = "PARENT")
    public void getAllSchedulesForExistedClassForRoleParentOfStudentFromClass() {
        when(scheduleService.findAllSchedulesForClass(any())).thenReturn(List.of(getDefaultScheduleDTO()));

        mockMvc.perform(MockMvcRequestBuilders.get(SCHEDULES_URL + "/all/1")
                        .accept(MediaType.TEXT_HTML).with(csrf()))
                .andExpect(status().is(HttpStatus.OK.value()))
                .andExpect(model().attributeExists("schedules"));
    }

    @Test
    @SneakyThrows
    @WithMockUser(roles = "PARENT")
    public void getAllSchedulesForExistedClassForRoleParentOfStudentNotFromClass() {
        doThrow(ApplicationForbiddenException.class).when(authService).hasRoleAdminOrStudentFromClassOrParentThatHasStudentInClass(any(), any());

        mockMvc.perform(MockMvcRequestBuilders.get(SCHEDULES_URL + "/all/1")
                        .accept(MediaType.TEXT_HTML).with(csrf()))
                .andExpect(status().is(HttpStatus.OK.value()))
                .andExpect(view().name("error/403"));
    }

    @Test
    @SneakyThrows
    @WithMockUser(roles = "STUDENT")
    public void getAllSchedulesForExistedClassForStudentNotFromClass() {
        doThrow(ApplicationForbiddenException.class).when(authService).hasRoleAdminOrStudentFromClassOrParentThatHasStudentInClass(any(), any());

        mockMvc.perform(MockMvcRequestBuilders.get(SCHEDULES_URL + "/all/1")
                        .accept(MediaType.TEXT_HTML).with(csrf()))
                .andExpect(status().is(HttpStatus.OK.value()))
                .andExpect(view().name("error/403"));
    }

    @Test
    @SneakyThrows
    @WithMockUser(roles = "STUDENT")
    public void getAllSchedulesForExistedClassForStudentFromClass() {
        when(scheduleService.findAllSchedulesForClass(any())).thenReturn(List.of(getDefaultScheduleDTO()));

        mockMvc.perform(MockMvcRequestBuilders.get(SCHEDULES_URL + "/all/1")
                        .accept(MediaType.TEXT_HTML).with(csrf()))
                .andExpect(status().is(HttpStatus.OK.value()))
                .andExpect(model().attributeExists("schedules"));
    }

    @Test
    @SneakyThrows
    public void getScheduleByIdForRoleAdminAndScheduleExists() {
        when(scheduleService.findById(any())).thenReturn(getDefaultScheduleDTO());

        mockMvc.perform(MockMvcRequestBuilders.get(SCHEDULES_URL + "/1")
                        .accept(MediaType.TEXT_HTML).with(csrf()))
                .andExpect(status().is(HttpStatus.OK.value()))
                .andExpect(view().name("schedule/schedule-class-show"))
                .andExpect(model().attributeExists("schedule"));
    }

    @Test
    @SneakyThrows
    public void getScheduleByIdForRoleAdminAndScheduleNotExists() {
        when(scheduleService.findById(any())).thenThrow(ScheduleNotFoundException.class);
        when(messageSource.getMessage(any(), any(), any())).thenReturn("Ячейка расписания с id=1 не найдена");

        mockMvc.perform(MockMvcRequestBuilders.get(SCHEDULES_URL + "/1")
                        .accept(MediaType.TEXT_HTML).with(csrf()))
                .andExpect(status().is(HttpStatus.OK.value()))
                .andExpect(view().name("error/error-app"))
                .andExpect(model().attributeExists("error"));
    }

    @Test
    @SneakyThrows
    @WithMockUser(roles = "TEACHER")
    public void getScheduleByIdForRoleTeacher() {
        doThrow(ApplicationForbiddenException.class).when(authService).hasRoleAdmin(any());

        mockMvc.perform(MockMvcRequestBuilders.get(SCHEDULES_URL + "/1")
                        .accept(MediaType.TEXT_HTML).with(csrf()))
                .andExpect(status().is(HttpStatus.OK.value()))
                .andExpect(view().name("error/403"));
    }

    @Test
    @SneakyThrows
    @WithMockUser(roles = "PARENT")
    public void getScheduleByIdForRoleParent() {
        doThrow(ApplicationForbiddenException.class).when(authService).hasRoleAdmin(any());

        mockMvc.perform(MockMvcRequestBuilders.get(SCHEDULES_URL + "/1")
                        .accept(MediaType.TEXT_HTML).with(csrf()))
                .andExpect(status().is(HttpStatus.OK.value()))
                .andExpect(view().name("error/403"));
    }

    @Test
    @SneakyThrows
    @WithMockUser(roles = "STUDENT")
    public void getScheduleByIdForRoleStudent() {
        doThrow(ApplicationForbiddenException.class).when(authService).hasRoleAdmin(any());

        mockMvc.perform(MockMvcRequestBuilders.get(SCHEDULES_URL + "/1")
                        .accept(MediaType.TEXT_HTML).with(csrf()))
                .andExpect(status().is(HttpStatus.OK.value()))
                .andExpect(view().name("error/403"));
    }

    @Test
    @SneakyThrows
    public void getPageForEditingScheduleForRoleAdminAndScheduleExists() {
        when(scheduleService.findById(any())).thenReturn(getDefaultScheduleDTO());

        mockMvc.perform(MockMvcRequestBuilders.get(SCHEDULES_URL + "/1/edit")
                        .accept(MediaType.TEXT_HTML).with(csrf()))
                .andExpect(status().is(HttpStatus.OK.value()))
                .andExpect(view().name("schedule/schedule-class-edit"))
                .andExpect(model().attributeExists("schedule"));
    }

    @Test
    @SneakyThrows
    public void getPageForEditingScheduleForRoleAdminAndScheduleNotExists() {
        when(scheduleService.findById(any())).thenThrow(ScheduleNotFoundException.class);
        when(messageSource.getMessage(any(), any(), any())).thenReturn("Ячейка расписания с id=1 не найдена");

        mockMvc.perform(MockMvcRequestBuilders.get(SCHEDULES_URL + "/1/edit")
                        .accept(MediaType.TEXT_HTML).with(csrf()))
                .andExpect(status().is(HttpStatus.OK.value()))
                .andExpect(view().name("error/error-app"))
                .andExpect(model().attributeExists("error"));
    }

    @Test
    @SneakyThrows
    @WithMockUser(roles = "TEACHER")
    public void getPageForEditingScheduleForRoleTeacher() {
        doThrow(ApplicationForbiddenException.class).when(authService).hasRoleAdmin(any());

        mockMvc.perform(MockMvcRequestBuilders.get(SCHEDULES_URL + "/1/edit")
                        .accept(MediaType.TEXT_HTML).with(csrf()))
                .andExpect(status().is(HttpStatus.OK.value()))
                .andExpect(view().name("error/403"));
    }

    @Test
    @SneakyThrows
    @WithMockUser(roles = "PARENT")
    public void getPageForEditingScheduleForRoleParent() {
        doThrow(ApplicationForbiddenException.class).when(authService).hasRoleAdmin(any());

        mockMvc.perform(MockMvcRequestBuilders.get(SCHEDULES_URL + "/1/edit")
                        .accept(MediaType.TEXT_HTML).with(csrf()))
                .andExpect(status().is(HttpStatus.OK.value()))
                .andExpect(view().name("error/403"));
    }

    @Test
    @SneakyThrows
    @WithMockUser(roles = "STUDENT")
    public void getPageForEditingScheduleForRoleStudent() {
        doThrow(ApplicationForbiddenException.class).when(authService).hasRoleAdmin(any());

        mockMvc.perform(MockMvcRequestBuilders.get(SCHEDULES_URL + "/1/edit")
                        .accept(MediaType.TEXT_HTML).with(csrf()))
                .andExpect(status().is(HttpStatus.OK.value()))
                .andExpect(view().name("error/403"));
    }

    @Test
    @SneakyThrows
    @DisplayName("Should return status 302 and page for delete existed schedule")
    public void deleteExistedScheduleForRoleAdmin() {
        mockMvc.perform(MockMvcRequestBuilders.delete(SCHEDULES_URL + "/1")
                        .accept(MediaType.TEXT_HTML).with(csrf()))
                .andExpect(status().is(HttpStatus.FOUND.value()));
    }

    @Test
    @SneakyThrows
    @DisplayName("Should return status 200 and error")
    public void deleteNotExistedScheduleForRoleAdmin() {
        doThrow(ScheduleNotFoundException.class).when(scheduleService).deleteById(any());
        when(messageSource.getMessage(any(), any(), any())).thenReturn("Ячейка расписания c id=1 не найдена");

        mockMvc.perform(MockMvcRequestBuilders.delete(SCHEDULES_URL + "/1")
                        .accept(MediaType.TEXT_HTML).with(csrf()))
                .andExpect(status().is(HttpStatus.OK.value()))
                .andExpect(view().name("error/error-app"))
                .andExpect(model().attributeExists("error"));
    }

    @Test
    @SneakyThrows
    @WithMockUser(roles = "ADMIN")
    @DisplayName("Should return status 200 and create new schedule")
    public void postCreateScheduleValidForRoleAdmin() {
        var payload = getDefaultScheduleCreateDTO();

        mockMvc.perform(MockMvcRequestBuilders.post(SCHEDULES_URL)
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .param("dayOfWeek", String.valueOf(payload.dayOfWeek()))
                        .param("numberOfClassroom", String.valueOf(payload.numberOfClassroom()))
                        .param("startTime", String.valueOf(payload.startTime()))
                        .param("classId", String.valueOf(payload.classId()))
                        .param("subjectId", payload.subjectId().toString())
                        .with(csrf()))
                .andExpect(status().is(HttpStatus.FOUND.value()));
    }

    @SneakyThrows
    @ParameterizedTest
    @DisplayName("Should return status 200 and errors")
    @MethodSource("provideInvalidScheduleCreateDTOs")
    public void postCreateNewScheduleInvalid(ScheduleCreateDTO scheduleCreateDTO) {
        checkPostCreateInvalidSchedule(scheduleCreateDTO);
    }

    @Test
    @SneakyThrows
    @WithMockUser(roles = "ADMIN")
    @DisplayName("Should return status 200 and create new schedule")
    public void putUpdateScheduleValidForRoleAdmin() {
        var payload = ScheduleUpdateDTO
                .builder()
                .dayOfWeek(1)
                .startTime(LocalTime.now())
                .numberOfClassroom(1)
                .build();

        mockMvc.perform(MockMvcRequestBuilders.put(SCHEDULES_URL+"/1")
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .param("dayOfWeek", String.valueOf(payload.dayOfWeek()))
                        .param("numberOfClassroom", String.valueOf(payload.numberOfClassroom()))
                        .param("startTime", String.valueOf(payload.startTime()))
                        .with(csrf()))
                .andExpect(status().is(HttpStatus.FOUND.value()));
    }

    @SneakyThrows
    @ParameterizedTest
    @DisplayName("Should return status 200 and errors")
    @MethodSource("provideInvalidScheduleUpdateDTOs")
    public void postUpdateScheduleInvalid(ScheduleUpdateDTO scheduleUpdateDTO) {
        checkPutCreateInvalidSchedule(scheduleUpdateDTO);
    }

    @SneakyThrows
    private void checkPostCreateInvalidSchedule(ScheduleCreateDTO scheduleCreateDTO) {
        mockMvc.perform(MockMvcRequestBuilders.post(SCHEDULES_URL)
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .param("dayOfWeek", String.valueOf(scheduleCreateDTO.dayOfWeek()))
                        .param("numberOfClassroom", String.valueOf(scheduleCreateDTO.numberOfClassroom()))
                        .param("startTime", String.valueOf(scheduleCreateDTO.startTime()))
                        .param("classId", String.valueOf(scheduleCreateDTO.classId()))
                        .param("subjectId", String.valueOf(scheduleCreateDTO.subjectId()))
                        .with(csrf()))
                .andExpect(status().is(HttpStatus.OK.value()))
                .andExpect(view().name("error/400"))
                .andExpect(model().attributeExists("errors"));
    }

    @SneakyThrows
    private void checkPutCreateInvalidSchedule(ScheduleUpdateDTO scheduleUpdateDTO) {
        mockMvc.perform(MockMvcRequestBuilders.put(SCHEDULES_URL+"/1")
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .param("dayOfWeek", String.valueOf(scheduleUpdateDTO.dayOfWeek()))
                        .param("numberOfClassroom", String.valueOf(scheduleUpdateDTO.numberOfClassroom()))
                        .param("startTime", String.valueOf(scheduleUpdateDTO.startTime()))
                        .with(csrf()))
                .andExpect(status().is(HttpStatus.OK.value()))
                .andExpect(view().name("error/400"))
                .andExpect(model().attributeExists("errors"));
    }
}
