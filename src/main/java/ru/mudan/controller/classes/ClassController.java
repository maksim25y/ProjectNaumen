package ru.mudan.controller.classes;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import ru.mudan.dto.classes.ClassDTO;
import ru.mudan.services.classes.ClassService;
import ru.mudan.services.students.StudentService;
import ru.mudan.services.subjects.SubjectService;
import static ru.mudan.controller.Util.doRedirect;

/**
 * Контроллер, принимающий запросы
 * для работы с классами
 */
@Controller
@RequestMapping("/classes")
@SuppressWarnings("MultipleStringLiterals")
@RequiredArgsConstructor
public class ClassController {

    private final ClassService classService;
    private final SubjectService subjectService;
    private final StudentService studentService;

    /**
     * Эндпоинт для получения всех классов
     */
    @GetMapping("/all")
    public String getPageWithInfoAboutAllClasses(Model model) {
        model.addAttribute("classes", classService.findAll());
        return "admin/classes/classes-index";
    }

    /**
     * Эндпоинт для получения класса по id
     *
     * @param id - id класса
     */
    @GetMapping("/{id}")
    public String getPageWithInfoAboutClassById(Model model, @PathVariable Long id) {
        var classDTO = classService.findById(id);
        model.addAttribute("cl", classService.findById(id));
        model.addAttribute("students", studentService.findAllStudentsForClass(classDTO.id()));
        model.addAttribute("subjects", subjectService.findAllSubjectsForClass(classDTO.id()));
        model.addAttribute("studentsForAdding", studentService.findStudentsWithNotClass());
        return "admin/classes/classes-show";
    }

    /**
     * Эндпоинт для добавления учеников в класс по id
     *
     * @param classId  - id класса
     * @param classDTO - данные с id учеников для добавления
     */
    @PostMapping("/{classId}/students")
    public String addStudentsToClass(@PathVariable Long classId,
                                     ClassDTO classDTO,
                                     HttpServletRequest request) {
        classService.addStudentsToClass(classId, classDTO.studentsIds());
        return doRedirect(request);
    }

    /**
     * Эндпоинт для добавления предметов в класс по id
     *
     * @param classId  - id класса
     * @param classDTO - данные с id предметов для добавления
     */
    @PostMapping("/{classId}/subjects")
    public String addSubjectsToClass(@PathVariable Long classId,
                                     ClassDTO classDTO,
                                     HttpServletRequest request) {
        classService.addSubjectsToClass(classId, classDTO.subjectsIds());
        return doRedirect(request);
    }

    /**
     * Эндпоинт для получения шаблона создания класса
     */
    @GetMapping("/add")
    public String getPageForCreatingNewClass(Model model) {
        model.addAttribute("students", studentService.findStudentsWithNotClass());
        return "admin/classes/classes-add";
    }

    /**
     * Эндпоинт для создания класса
     *
     * @param classDTO - входные данные для создания класса
     */
    @PostMapping
    public String createNewClass(@Valid ClassDTO classDTO, HttpServletRequest request) {
        classService.save(classDTO);
        return doRedirect(request);
    }

    /**
     * Эндпоинт для получения шаблона для редактирования класса
     *
     * @param id - id класса
     */
    @GetMapping("/{id}/edit")
    public String getPageForEditingClass(@PathVariable Long id, Model model) {
        model.addAttribute("cl", classService.findById(id));
        return "admin/classes/classes-edit";
    }

    /**
     * Эндпоинт для обновления класса
     *
     * @param id       - id класса
     * @param classDTO - входные данные для обновления класса
     */
    @PutMapping("/{id}")
    public String updateClass(@PathVariable Long id, @Valid ClassDTO classDTO, HttpServletRequest request) {
        classService.update(classDTO, id);
        return "redirect:/classes/" + id;
    }

    /**
     * Эндпоинт для удаления класса по id
     *
     * @param id - id класса
     */
    @DeleteMapping("/{id}")
    public String deleteClass(@PathVariable Long id) {
        classService.deleteById(id);
        return "redirect:/classes/all";
    }
}
