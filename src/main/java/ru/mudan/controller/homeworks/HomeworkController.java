package ru.mudan.controller.homeworks;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import ru.mudan.dto.homework.HomeworkCreateDTO;
import ru.mudan.dto.homework.HomeworkDTO;
import ru.mudan.services.auth.AuthService;
import ru.mudan.services.classes.ClassService;
import ru.mudan.services.homework.HomeworkService;
import ru.mudan.services.subjects.SubjectService;

/**
 * Контроллер, принимающий запросы
 * для работы с ДЗ
 */
@Controller
@RequestMapping("/homeworks")
@SuppressWarnings({"MemberName", "MultipleStringLiterals"})
@RequiredArgsConstructor
public class HomeworkController {

    private final String HOMEWORK = "homework";
    private final HomeworkService homeworkService;
    private final ClassService classService;
    private final SubjectService subjectService;
    private final AuthService authService;

    /**
     * Эндпоинт для получения ДЗ для класса
     *
     * @param classId   - id класса
     * @param subjectId - id предмета
     */
    @GetMapping("/all/{classId}")
    public String getPageWithInfoAboutAllHomeworksWithSubjectIdAndClassId(Model model,
                                                                          @PathVariable Long classId,
                                                                          @RequestParam(required = false)
                                                                          Long subjectId,
                                                                          Authentication authentication) {
        authService.hasRoleAdminOrStudentFromClassOrParentThatHasStudentInClass(
                classId,
                authentication);
        if (subjectId != null) {
            model.addAttribute("subject", subjectService.findById(subjectId));
            model.addAttribute("homeworks", homeworkService.findAllByClassAndSubject(classId, subjectId));
        } else {
            model.addAttribute("homeworks", homeworkService.findAllByClass(classId));
        }
        model.addAttribute("class", classService.findById(classId));
        return "admin/homeworks/homeworks-index";
    }

    /**
     * Эндпоинт для создания ДЗ
     *
     * @param homeworkDTO - входные данные для создания ДЗ
     */
    @PostMapping
    public String createNewHomework(@Valid HomeworkCreateDTO homeworkDTO, Authentication authentication) {
        authService.teacherHasSubjectOrRoleIsAdmin(homeworkDTO.subjectId(), authentication);
        homeworkService.save(homeworkDTO);
        return "redirect:/";
    }

    /**
     * Эндпоинт для получения ДЗ по id
     *
     * @param id - id ДЗ
     */
    @GetMapping("/{id}")
    public String getPageWithInfoAboutHomeworkById(@PathVariable("id") Long id,
                                                   Model model,
                                                   Authentication authentication) {
        authService.teacherHasHomeworkOrRoleIsAdmin(id, authentication);
        var foundHomework = homeworkService.findById(id);
        model.addAttribute(HOMEWORK, homeworkService.findById(id));
        model.addAttribute("class", classService.findById(foundHomework.classId()));
        model.addAttribute("subject", subjectService.findById(foundHomework.subjectId()));
        return "admin/homeworks/homeworks-show";
    }

    /**
     * Эндпоинт для получения шаблона для редактирования ДЗ
     *
     * @param id - id ДЗ
     */
    @GetMapping("/{id}/edit")
    public String getPageForEditingHomework(@PathVariable("id") Long id, Model model, Authentication authentication) {
        authService.teacherHasHomeworkOrRoleIsAdmin(id, authentication);
        model.addAttribute(HOMEWORK, homeworkService.findById(id));
        return "admin/homeworks/homeworks-edit";
    }

    /**
     * Эндпоинт для удаления ДЗ по id
     *
     * @param id - id ДЗ
     */
    @DeleteMapping("/{id}")
    public String deleteHomework(@PathVariable("id") Long id, Authentication authentication) {
        authService.teacherHasHomeworkOrRoleIsAdmin(id, authentication);
        homeworkService.delete(id);
        return "redirect:/";
    }

    /**
     * Эндпоинт для обновления ДЗ
     *
     * @param id          - id ДЗ
     * @param homeworkDTO - входные данные для обновления ДЗ
     */
    @PutMapping("/{id}")
    public String updateHomework(@Valid HomeworkDTO homeworkDTO,
                                 @PathVariable("id") Long id,
                                 Authentication authentication) {
        authService.teacherHasHomeworkOrRoleIsAdmin(id, authentication);
        homeworkService.update(id, homeworkDTO);
        return "redirect:/homeworks/" + id;
    }
}
