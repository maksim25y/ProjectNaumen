package ru.mudan.controller.teacher;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import ru.mudan.services.grades.GradesService;
import ru.mudan.services.homework.HomeworkService;
import ru.mudan.services.schedule.ScheduleService;
import ru.mudan.services.subjects.SubjectService;
import ru.mudan.services.teachers.TeacherService;

@Controller
@RequiredArgsConstructor
@RequestMapping("/teacher")
public class TeacherController {

    private final TeacherService teacherService;
    private final SubjectService subjectService;
    private final HomeworkService homeworkService;
    private final ScheduleService scheduleService;
    private final GradesService gradesService;

    @GetMapping("/account")
    public String getTeacherMainPage(Authentication authentication, Model model) {
        var teacher = teacherService.findTeacherByAuth(authentication);
        model.addAttribute("teacher", teacher);
        model.addAttribute("subjects", subjectService.getSubjectsForTeacher(teacher.id()));
        return "teacher/teacher-main-page";
    }

    @GetMapping("/hw/{subjectId}")
    public String hwTeacherSubject(Model model,
                                   @PathVariable Long subjectId,
                                   Authentication authentication) {
        subjectService.teacherContainSubject(subjectId, authentication);

        model.addAttribute("homeworks", homeworkService.findAllBySubject(subjectId));
        return "teacher/homework/homeworks-show";
    }

    @GetMapping("/schedule/{subjectId}")
    public String scheduleTeacher(Model model,
                                   @PathVariable Long subjectId,
                                   Authentication authentication) {
        subjectService.teacherContainSubject(subjectId, authentication);

        model.addAttribute("schedules", scheduleService.findAllBySubjectId(subjectId));
        return "teacher/schedule/schedule-teacher-index";
    }

    @GetMapping("/grades/subject/{subjectId}")
    public String gradesTeacher(Model model,
                                  @PathVariable Long subjectId,
                                  Authentication authentication) {
        subjectService.teacherContainSubject(subjectId, authentication);

        model.addAttribute("grades", gradesService.findAllBySubjectId(subjectId));
        return "teacher/grades/grades-index";
    }
}
