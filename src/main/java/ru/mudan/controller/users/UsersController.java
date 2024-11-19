package ru.mudan.controller.users;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import ru.mudan.dto.users.UserUpdateDTO;
import ru.mudan.services.auth.MyUserDetailsService;
import ru.mudan.services.parent.ParentService;
import ru.mudan.services.students.StudentService;
import ru.mudan.services.teachers.TeacherService;

@SuppressWarnings("MultipleStringLiterals")
@Controller
@RequestMapping("/users")
@RequiredArgsConstructor
public class UsersController {

    private final TeacherService teacherService;
    private final MyUserDetailsService myUserDetailsService;
    private final ParentService parentService;
    private final StudentService studentService;

    @GetMapping("/teachers/all")
    public String getAllTeachers(Model model) {
        model.addAttribute("teachers", teacherService.findAll());
        return "admin/users/teacher/teacher-index";
    }

    @GetMapping("/teachers/{id}")
    public String getTeacherById(@PathVariable("id") Long id, Model model) {
        model.addAttribute("teacher", teacherService.findTeacherById(id));
        return "admin/users/teacher/teacher-show";
    }

    @GetMapping("/teachers/{id}/edit")
    public String editTeacherById(@PathVariable("id") Long id, Model model) {
        model.addAttribute("user", teacherService.findTeacherById(id));
        return "admin/users/edit";
    }

    @GetMapping("/parents/all")
    public String getAllParents(Model model) {
        model.addAttribute("parents", parentService.findAllParents());
        return "admin/users/parent/parent-index";
    }

    @GetMapping("/parents/{id}")
    public String getParentById(@PathVariable("id") Long id, Model model) {
        model.addAttribute("parent", parentService.findParentById(id));
        return "admin/users/parent/parent-show";
    }

    @GetMapping("/parents/{id}/edit")
    public String editParentById(@PathVariable("id") Long id, Model model) {
        model.addAttribute("user", parentService.findParentById(id));
        return "admin/users/edit";
    }

    @GetMapping("/students/all")
    public String getAllStudents(Model model) {
        model.addAttribute("students", studentService.findAll());
        return "admin/users/student/student-index";
    }

    @GetMapping("/students/{id}")
    public String getStudentById(@PathVariable("id") Long id, Model model) {
        model.addAttribute("student", studentService.findById(id));
        return "admin/users/student/student-show";
    }

    @GetMapping("/students/{id}/edit")
    public String editStudentById(@PathVariable("id") Long id, Model model) {
        model.addAttribute("user", studentService.findById(id));
        return "admin/users/edit";
    }

    @PutMapping("/{email}")
    public String updateUserWith(@PathVariable String email, @Valid UserUpdateDTO userUpdateDTO) {
        myUserDetailsService.updateUserByEmail(email, userUpdateDTO);
        return "redirect:/";
    }
}
