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

    @PostMapping
    public String createNewHomework(@Valid HomeworkCreateDTO homeworkDTO, Authentication authentication) {
        authService.teacherHasSubjectOrRoleIsAdmin(homeworkDTO.subjectId(), authentication);
        homeworkService.save(homeworkDTO);
        return "redirect:/";
    }

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

    @GetMapping("/{id}/edit")
    public String getPageForEditingHomework(@PathVariable("id") Long id, Model model, Authentication authentication) {
        authService.teacherHasHomeworkOrRoleIsAdmin(id, authentication);
        model.addAttribute(HOMEWORK, homeworkService.findById(id));
        return "admin/homeworks/homeworks-edit";
    }

    @DeleteMapping("/{id}")
    public String deleteHomework(@PathVariable("id") Long id, Authentication authentication) {
        authService.teacherHasHomeworkOrRoleIsAdmin(id, authentication);
        homeworkService.delete(id);
        return "redirect:/";
    }

    @PutMapping("/{id}")
    public String updateHomework(@Valid HomeworkDTO homeworkDTO,
                                 @PathVariable("id") Long id,
                                 Authentication authentication) {
        authService.teacherHasHomeworkOrRoleIsAdmin(id, authentication);
        homeworkService.update(id, homeworkDTO);
        return "redirect:/homeworks/" + id;
    }
}
