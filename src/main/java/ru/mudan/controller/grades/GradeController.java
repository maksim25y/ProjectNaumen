package ru.mudan.controller.grades;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import ru.mudan.dto.grades.GradeDTO;
import ru.mudan.services.auth.AuthService;
import ru.mudan.services.grades.GradesService;
import ru.mudan.services.students.StudentService;
import ru.mudan.services.subjects.SubjectService;
import static ru.mudan.controller.Util.doRedirect;

/**
 * Контроллер, принимающий запросы
 * для работы с оценками
 */
@Controller
@RequestMapping("/grades")
@RequiredArgsConstructor
@SuppressWarnings("MultipleStringLiterals")
public class GradeController {

    private final GradesService gradesService;
    private final SubjectService subjectService;
    private final StudentService studentService;
    private final AuthService authService;

    /**
     * Эндпоинт для получения оценок ученика
     *
     * @param studentId - id ученика
     * @param subjectId - id предмета
     */
    @GetMapping("/all/{studentId}")
    public String getPageWithInfoAboutAllGrades(Model model, @PathVariable Long studentId,
                                                @RequestParam(value = "subjectId", required = false) Long subjectId,
                                                Authentication auth) {
        authService.hasRoleAdminOrStudentInClassWithSubjectOrParentHasStudentInClass(
                studentId, subjectId, auth);
        if (subjectId != null) {
            model.addAttribute("subject", subjectService.findById(subjectId));
            model.addAttribute("grades", gradesService.findAllGradesForStudentWithSubject(studentId, subjectId));
        } else {
            model.addAttribute("grades", gradesService.findAllGradesForStudent(studentId));
        }
        model.addAttribute("student", studentService.findById(studentId));
        return "grades/grades-index";
    }

    /**
     * Эндпоинт для получения оценки по id
     *
     * @param id - id оценки
     */
    @GetMapping("/{id}")
    public String getPageWithInfoAboutGradeById(@PathVariable Long id, Model model, Authentication auth) {
        authService.teacherHasGradeOrRoleIsAdmin(id, auth);
        model.addAttribute("grade", gradesService.findById(id));
        return "grades/grades-show";
    }

    /**
     * Эндпоинт для создания оценки
     *
     * @param gradeDTO - входные данные для создания оценки
     */
    @PostMapping
    public String createNewGrade(@Valid GradeDTO gradeDTO, Authentication auth, HttpServletRequest request) {
        authService.teacherHasSubjectOrRoleIsAdmin(gradeDTO.subjectId(), auth);
        gradesService.save(gradeDTO);
        return doRedirect(request);
    }

    /**
     * Эндпоинт для получения шаблона для редактирования оценки
     *
     * @param id - id оценки
     */
    @GetMapping("/{id}/edit")
    public String getPageForEditingGrade(@PathVariable Long id, Model model, Authentication auth) {
        authService.teacherHasGradeOrRoleIsAdmin(id, auth);
        model.addAttribute("grade", gradesService.findById(id));
        return "grades/grades-edit";
    }

    /**
     * Эндпоинт для обновления оценки
     *
     * @param id       - id оценки
     * @param gradeDTO - входные данные для обновления оценки
     */
    @PutMapping("/{id}")
    public String updateGrade(@PathVariable Long id, @Valid GradeDTO gradeDTO, Authentication auth) {
        authService.teacherHasGradeOrRoleIsAdmin(id, auth);
        gradesService.update(gradeDTO, id);
        return "redirect:/grades/" + id;
    }

    /**
     * Эндпоинт для удаления оценки по id
     *
     * @param id - id оценки
     */
    @DeleteMapping("/{id}")
    public String deleteGrade(@PathVariable Long id, Authentication auth, HttpServletRequest request) {
        authService.teacherHasGradeOrRoleIsAdmin(id, auth);
        gradesService.deleteById(id);
        return "redirect:/";
    }
}
