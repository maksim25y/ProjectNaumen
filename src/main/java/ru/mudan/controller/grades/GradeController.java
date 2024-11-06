package ru.mudan.controller.grades;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import ru.mudan.dto.grades.GradeDTO;
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

    @GetMapping("/{id}")
    public String getGradeById(@PathVariable Long id, Model model) {
        model.addAttribute("grade", gradesService.findById(id));
        return "grades/grades-show";
    }

    @PostMapping
    public String saveGrade(GradeDTO gradeDTO) {
        gradesService.save(gradeDTO);
        return "redirect:/classes/all";
    }

    @GetMapping("/{id}/edit")
    public String editGrade(@PathVariable Long id, Model model) {
        model.addAttribute("grade", gradesService.findById(id));
        return "grades/grades-edit";
    }

    @PutMapping("/{id}")
    public String updateGrade(@PathVariable Long id, GradeDTO gradeDTO) {
        gradesService.update(gradeDTO, id);
        return "redirect:/grades/" + id;
    }

    @DeleteMapping("/{id}")
    public String deleteGrade(@PathVariable Long id) {
        gradesService.deleteById(id);
        return "redirect:/classes/all";
    }
}
