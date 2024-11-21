package ru.mudan.controller.teacher;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import ru.mudan.services.auth.AuthService;
import ru.mudan.services.grades.GradesService;
import ru.mudan.services.homework.HomeworkService;
import ru.mudan.services.schedule.ScheduleService;
import ru.mudan.services.students.StudentService;
import ru.mudan.services.subjects.SubjectService;
import ru.mudan.services.teachers.TeacherService;

/**
 * Контроллер, принимающий запросы
 * только от учителя
 */
@Controller
@RequiredArgsConstructor
@RequestMapping("/teacher")
public class TeacherController {

    private final TeacherService teacherService;
    private final SubjectService subjectService;
    private final HomeworkService homeworkService;
    private final ScheduleService scheduleService;
    private final GradesService gradesService;
    private final StudentService studentService;
    private final AuthService authService;

    /**
     * Эндпоинт для получения шаблона аккаунта учителя
     */
    @GetMapping("/account")
    public String getTeacherMainPage(Authentication authentication, Model model) {
        var teacher = teacherService.findTeacherByAuth(authentication);
        model.addAttribute("teacher", teacher);
        model.addAttribute("subjects", subjectService.getSubjectsForTeacher(teacher.id()));
        return "teacher/teacher-main-page";
    }

    /**
     * Эндпоинт для получения всех ДЗ учителя по предмету
     * !Только по предмету, который учитель ведёт
     *
     * @param subjectId - id предмета
     */
    @GetMapping("/hw/{subjectId}")
    public String hwTeacherSubject(Model model,
                                   @PathVariable Long subjectId,
                                   Authentication authentication) {
        authService.teacherContainSubject(subjectId, authentication);

        model.addAttribute("homeworks", homeworkService.findAllBySubject(subjectId));
        return "teacher/homework/homeworks-show";
    }

    /**
     * Эндпоинт для получения расписания учителя по предмету
     * !Только по предмету, который учитель ведёт
     *
     * @param subjectId - id предмета
     */
    @GetMapping("/schedule/{subjectId}")
    public String scheduleTeacher(Model model,
                                  @PathVariable Long subjectId,
                                  Authentication authentication) {
        authService.teacherContainSubject(subjectId, authentication);

        model.addAttribute("schedules", scheduleService.findAllBySubjectId(subjectId));
        return "teacher/schedule/schedule-teacher-index";
    }

    /**
     * Эндпоинт для получения оценок учителя по предмету
     * !Только по предмету, который учитель ведёт
     *
     * @param subjectId - id предмета
     */
    @GetMapping("/grades/subject/{subjectId}")
    public String gradesTeacher(Model model,
                                @PathVariable Long subjectId,
                                Authentication authentication) {
        authService.teacherContainSubject(subjectId, authentication);

        model.addAttribute("subject", subjectService.findById(subjectId));
        model.addAttribute("grades", gradesService.findAllBySubjectId(subjectId));
        model.addAttribute("students", studentService.findAllStudentsBySubjectId(subjectId));
        return "teacher/grades/grades-index";
    }
}
