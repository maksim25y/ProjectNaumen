package ru.mudan.controllers;

import lombok.experimental.UtilityClass;
import ru.mudan.dto.classes.ClassDTO;
import ru.mudan.dto.homework.HomeworkDTO;
import ru.mudan.dto.schedule.ScheduleCreateDTO;
import ru.mudan.dto.schedule.ScheduleDTO;
import ru.mudan.dto.subjects.SubjectDTO;

import java.time.LocalTime;
import java.util.List;

import static java.time.LocalDate.now;

@UtilityClass
public class UtilConstants {
    public static final String AUTH_URL ="/registration";
    public static final String SUBJECTS_URL ="/subjects";
    public static final String CLASSES_URL ="/classes";
    public static final String HOMEWORKS_URL ="/homeworks";
    public static final String SCHEDULES_URL ="/schedules";

    public static HomeworkDTO getDefaultHomeworkDTO() {
        return HomeworkDTO
                .builder()
                .id(1L)
                .title("Решить примеры")
                .description("Решить примеры на странице 5")
                .deadline(now())
                .classId(1L)
                .subjectId(1L)
                .build();
    }

    public static SubjectDTO getDefaultSubjectDTO() {
        return SubjectDTO
                .builder()
                .id(1L)
                .name("Математика")
                .type("Базовый")
                .description("Тестовое описание")
                .classId(1L)
                .build();
    }

    public static ClassDTO getDefaultClassDTO() {
        return ClassDTO
                .builder()
                .id(1L)
                .number(6)
                .letter("А")
                .studentsIds(List.of(1L, 2L, 3L))
                .subjectsIds(List.of(1L))
                .description("Тестовое описание класса")
                .build();
    }

    public static ScheduleCreateDTO getDefaultScheduleCreateDTO() {
        return ScheduleCreateDTO
                .builder()
                .dayOfWeek(1)
                .numberOfClassroom(1)
                .startTime(LocalTime.now())
                .classId(1L)
                .subjectId(1L)
                .build();
    }

    public static ScheduleDTO getDefaultScheduleDTO() {
        return ScheduleDTO
                .builder()
                .id(1L)
                .startTime(LocalTime.now())
                .dayOfWeek("Вторник")
                .numberOfClassRoom(10)
                .subjectName("Математика")
                .build();
    }
}
