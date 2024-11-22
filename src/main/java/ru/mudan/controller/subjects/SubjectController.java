package ru.mudan.controller.subjects;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import ru.mudan.dto.subjects.SubjectCreateDTO;
import ru.mudan.dto.subjects.SubjectUpdateDTO;
import ru.mudan.services.classes.ClassService;
import ru.mudan.services.subjects.SubjectService;
import ru.mudan.services.teachers.TeacherService;
import static ru.mudan.controller.Util.doRedirect;

@Controller
@SuppressWarnings("MultipleStringLiterals")
@RequestMapping("/subjects")
@RequiredArgsConstructor
public class SubjectController {

    private final SubjectService subjectService;
    private final ClassService classService;
    private final TeacherService teacherService;

    /**
     * Эндпоинт для получения всех предметов
     */
    @GetMapping("/all")
    public String getPageWithInfoAboutAllSubjects(Model model) {
        model.addAttribute("subjects", subjectService.findAll());
        return "admin/subjects/subjects-index";
    }

    /**
     * Эндпоинт для получения предмета по id
     *
     * @param id - id предмета
     */
    @GetMapping("/{id}")
    public String getPageWithInfoAboutSubjectById(Model model, @PathVariable Long id) {
        model.addAttribute("subject", subjectService.findById(id));
        return "admin/subjects/subjects-show";
    }

    /**
     * Эндпоинт для получения шаблона создания предмета
     */
    @GetMapping("/add")
    public String getPageForCreatingSubject(Model model) {
        model.addAttribute("classes", classService.findAll());
        model.addAttribute("teachers", teacherService.findAll());
        return "admin/subjects/subjects-add";
    }

    /**
     * Эндпоинт для создания предмета
     *
     * @param subjectDTO - входные данные для создания предмета
     */
    @PostMapping
    public String createSubject(@Valid SubjectCreateDTO subjectDTO, HttpServletRequest request) {
        subjectService.save(subjectDTO);
        return doRedirect(request);
    }

    /**
     * Эндпоинт для получения шаблона для редактирования предмета
     *
     * @param id - id предмета
     */
    @GetMapping("/{id}/edit")
    public String getPageForEditingSubject(Model model, @PathVariable Long id) {
        model.addAttribute("subject", subjectService.findById(id));
        return "admin/subjects/subjects-edit";
    }

    /**
     * Эндпоинт для обновления предмета
     *
     * @param id         - id предмета
     * @param subjectDTO - входные данные для обновления предмета
     */
    @PutMapping("/{id}")
    public String updateSubject(@Valid SubjectUpdateDTO subjectDTO, @PathVariable Long id) {
        subjectService.update(subjectDTO, id);
        return "redirect:/subjects/" + id;
    }

    /**
     * Эндпоинт для удаления предмета по id
     *
     * @param id - id предмета
     */
    @DeleteMapping("/{id}")
    public String deleteSubject(@PathVariable Long id) {
        subjectService.deleteById(id);
        return "redirect:/subjects/all";
    }
}
