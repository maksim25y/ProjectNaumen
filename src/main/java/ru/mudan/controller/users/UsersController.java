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

/**
 * Контроллер, принимающий запросы
 * на обновление удаление данных пользователей
 */
@SuppressWarnings("MultipleStringLiterals")
@Controller
@RequestMapping("/users")
@RequiredArgsConstructor
public class UsersController {

    private final TeacherService teacherService;
    private final MyUserDetailsService myUserDetailsService;
    private final ParentService parentService;
    private final StudentService studentService;

    /**
     * Эндпоинт для получения всех учителей
     */
    @GetMapping("/teachers/all")
    public String getAllTeachers(Model model) {
        model.addAttribute("teachers", teacherService.findAll());
        return "admin/users/teacher/teacher-index";
    }

    /**
     * Эндпоинт для получения учителя по id
     *
     * @param id - id учителя
     */
    @GetMapping("/teachers/{id}")
    public String getTeacherById(@PathVariable("id") Long id, Model model) {
        model.addAttribute("teacher", teacherService.findTeacherById(id));
        return "admin/users/teacher/teacher-show";
    }

    /**
     * Эндпоинт для получения шаблона для редактирования учителя
     *
     * @param id - id учителя
     */
    @GetMapping("/teachers/{id}/edit")
    public String editTeacherById(@PathVariable("id") Long id, Model model) {
        model.addAttribute("user", teacherService.findTeacherById(id));
        return "admin/users/edit";
    }

    /**
     * Эндпоинт для получения всех родителей
     */
    @GetMapping("/parents/all")
    public String getAllParents(Model model) {
        model.addAttribute("parents", parentService.findAllParents());
        return "admin/users/parent/parent-index";
    }

    /**
     * Эндпоинт для получения родителя по id
     *
     * @param id - id родителя
     */
    @GetMapping("/parents/{id}")
    public String getParentById(@PathVariable("id") Long id, Model model) {
        model.addAttribute("parent", parentService.findParentById(id));
        return "admin/users/parent/parent-show";
    }

    /**
     * Эндпоинт для получения шаблона для редактирования родителя
     *
     * @param id - id родителя
     */
    @GetMapping("/parents/{id}/edit")
    public String editParentById(@PathVariable("id") Long id, Model model) {
        model.addAttribute("user", parentService.findParentById(id));
        return "admin/users/edit";
    }

    /**
     * Эндпоинт для получения всех учеников
     */
    @GetMapping("/students/all")
    public String getAllStudents(Model model) {
        model.addAttribute("students", studentService.findAll());
        return "admin/users/student/student-index";
    }

    /**
     * Эндпоинт для получения ученика по id
     *
     * @param id - id ученика
     */
    @GetMapping("/students/{id}")
    public String getStudentById(@PathVariable("id") Long id, Model model) {
        model.addAttribute("student", studentService.findById(id));
        return "admin/users/student/student-show";
    }

    /**
     * Эндпоинт для получения шаблона для редактирования ученика
     *
     * @param id - id ученика
     */
    @GetMapping("/students/{id}/edit")
    public String editStudentById(@PathVariable("id") Long id, Model model) {
        model.addAttribute("user", studentService.findById(id));
        return "admin/users/edit";
    }

    /**
     * Эндпоинт для обновления пользователя по email
     *
     * @param userUpdateDTO - входные данные для обновления информации о пользователе
     */
    @PutMapping
    public String updateUser(@Valid UserUpdateDTO userUpdateDTO) {
        myUserDetailsService.updateUser(userUpdateDTO);
        return "redirect:/";
    }

    /**
     * Эндпоинт для удаления пользователя по email
     *
     * @param email - email пользователя для удаления
     */
    @DeleteMapping
    public String deleteUserByEmail(String email) {
        myUserDetailsService.deleteUserByEmail(email);
        return "redirect:/";
    }
}
