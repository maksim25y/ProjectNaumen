package ru.mudan;

import java.time.LocalTime;
import java.util.List;
import lombok.experimental.UtilityClass;
import ru.mudan.dto.auth.RegisterUserDTO;
import ru.mudan.dto.classes.ClassDTO;
import ru.mudan.dto.grades.GradeDTO;
import ru.mudan.dto.grades.GradeDTOResponse;
import ru.mudan.dto.homework.HomeworkCreateDTO;
import ru.mudan.dto.homework.HomeworkDTO;
import ru.mudan.dto.parent.ParentDTO;
import ru.mudan.dto.schedule.ScheduleCreateDTO;
import ru.mudan.dto.schedule.ScheduleDTO;
import ru.mudan.dto.student.StudentDTO;
import ru.mudan.dto.subjects.SubjectCreateDTO;
import ru.mudan.dto.subjects.SubjectDTO;
import ru.mudan.dto.teacher.TeacherDTO;

import static java.time.LocalDate.now;

@UtilityClass
public class UtilConstants {
    public static final String AUTH_URL ="/registration";
    public static final String SUBJECTS_URL ="/subjects";
    public static final String CLASSES_URL ="/classes";
    public static final String HOMEWORKS_URL ="/homeworks";
    public static final String SCHEDULES_URL ="/schedules";
    public static final String GRADES_URL ="/grades";
    public static final String PARENT_URL ="/parent";
    public static final String STUDENT_URL = "/student";
    public static final String TEACHER_URL = "/teacher";

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

    public static GradeDTO getDefaultGradeDTO() {
        return GradeDTO
                .builder()
                .id(1L)
                .mark(4)
                .subjectId(1L)
                .comment("Хорошая работа")
                .dateOfMark(now())
                .studentId(1L)
                .build();
    }

    public static StudentDTO getDefaultStudentDTO() {
        return StudentDTO
                .builder()
                .id(1L)
                .firstname("Иван")
                .lastname("Иванов")
                .patronymic("Иванович")
                .email("test@mail.ru")
                .classId(1L)
                .build();
    }

    public static TeacherDTO getDefaultTeacherDTO() {
        return TeacherDTO
                .builder()
                .id(1L)
                .email("test@mail.ru")
                .firstname("Иван")
                .lastname("Иванов")
                .patronymic("Иванович")
                .build();
    }

    public static ParentDTO getDefaultParent() {
        return ParentDTO
                .builder()
                .id(1L)
                .email("test@mail.ru")
                .firstname("Иван")
                .lastname("Иванов")
                .patronymic("Иванович")
                .build();
    }

    public static GradeDTOResponse getDefaultGradeDTOResponse() {
        return GradeDTOResponse.builder()
                .id(1L)
                .mark(4)
                .studentFirstname("Иван")
                .studentLastname("Иванов")
                .comment("Хорошая работа")
                .dateOfMark(now())
                .build();
    }

    public RegisterUserDTO getDefaultRegisterUserDTO() {
        return RegisterUserDTO
                .builder()
                .firstname("Максим")
                .lastname("Максимов")
                .patronymic("Максимович")
                .password("test1234")
                .email("email@mail.com")
                .build();
    }

    public SubjectCreateDTO createSubjectDTOByClassIdAndTeacherId(Long classId, Long teacherId) {
        return SubjectCreateDTO
                .builder()
                .name("Математика")
                .type("Базовый")
                .description("Тестовое описание")
                .teacherId(teacherId)
                .classId(classId)
                .build();
    }

    public HomeworkCreateDTO createHomeworkCreateDTOBySubjectId(Long subjectId) {
        return HomeworkCreateDTO
                .builder()
                .title("Тест пройти")
                .description("На странице 5")
                .deadline(now())
                .subjectId(subjectId)
                .build();
    }

}
