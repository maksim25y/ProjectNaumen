package ru.mudan.controller.classes;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import ru.mudan.dto.classes.ClassDTO;
import ru.mudan.services.classes.ClassService;
import ru.mudan.services.students.StudentService;
import ru.mudan.services.subjects.SubjectService;

@Controller
@RequestMapping("/classes")
@SuppressWarnings({"MemberName", "MultipleStringLiterals"})
@RequiredArgsConstructor
public class ClassController {

    private final String REDIRECT_CLASSES_ALL = "redirect:/classes/all";
    private final ClassService classService;
    private final SubjectService subjectService;
    private final StudentService studentService;

    @GetMapping("/all")
    public String getPageWithInfoAboutAllClasses(Model model) {
        model.addAttribute("classes", classService.findAll());
        return "admin/classes/classes-index";
    }

    @GetMapping("/{id}")
    public String getPageWithInfoAboutClassById(Model model, @PathVariable Long id) {
        var classDTO = classService.findById(id);
        model.addAttribute("cl", classService.findById(id));
        model.addAttribute("students", studentService.findAllStudentsForClass(classDTO.id()));
        model.addAttribute("subjects", subjectService.findAllSubjectsForClass(classDTO.id()));
        model.addAttribute("studentsForAdding", studentService.findStudentsWithNotClass());
        return "admin/classes/classes-show";
    }

    @PostMapping("/{classId}/students")
    public String addStudentsToClass(@PathVariable Long classId,
                             ClassDTO classDTO) {
        classService.addStudentsToClass(classId, classDTO.studentsIds());
        return "redirect:/classes/" + classId;
    }

    @PostMapping("/{classId}/subjects")
    public String addSubjectsToClass(@PathVariable Long classId,
                             ClassDTO classDTO) {
        classService.addSubjectsToClass(classId, classDTO.subjectsIds());
        return "redirect:/classes/" + classId;
    }

    @GetMapping("/add")
    public String getPageForCreatingNewClass(Model model) {
        model.addAttribute("students", studentService.findStudentsWithNotClass());
        model.addAttribute("subjects", subjectService.findSubjectsWithNotClass());
        return "admin/classes/classes-add";
    }

    @PostMapping
    public String createNewClass(@Valid ClassDTO classDTO) {
        classService.save(classDTO);
        return REDIRECT_CLASSES_ALL;
    }

    @GetMapping("/{id}/edit")
    public String getPageForEditingClass(@PathVariable Long id, Model model) {
        model.addAttribute("cl", classService.findById(id));
        return "admin/classes/classes-edit";
    }

    @PutMapping("/{id}")
    public String updateClass(@PathVariable Long id, @Valid ClassDTO classDTO) {
        classService.update(classDTO, id);
        return "redirect:/classes/" + id;
    }

    @DeleteMapping("/{id}")
    public String deleteClass(@PathVariable Long id) {
        classService.deleteById(id);
        return REDIRECT_CLASSES_ALL;
    }
}
