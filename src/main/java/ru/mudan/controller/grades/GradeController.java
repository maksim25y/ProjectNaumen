package ru.mudan.controller.grades;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import ru.mudan.services.grades.GradesService;
import ru.mudan.services.students.StudentService;
import ru.mudan.services.subjects.SubjectService;

@Controller
@RequestMapping("/grades")
@RequiredArgsConstructor
@SuppressWarnings("MultipleStringLiterals")
public class GradeController {

    private final GradesService gradesService;
    private final SubjectService subjectService;
    private final StudentService studentService;

    @GetMapping("/all/{studentId}")
    public String all(Model model, @PathVariable Long studentId,
                      @RequestParam(value = "subjectId", required = false) Long subjectId) {
        if (subjectId != null) {
            model.addAttribute("subject", subjectService.findById(subjectId));
            model.addAttribute("grades", gradesService.findAllGradesForStudentWithSubject(studentId, subjectId));
        } else {
            model.addAttribute("grades", gradesService.findAllGradesForStudent(studentId));
        }
        model.addAttribute("student", studentService.findById(studentId));
        return "grades/grades-index";
    }
}
