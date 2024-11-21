package ru.mudan.controller.schedule;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import ru.mudan.dto.schedule.ScheduleCreateDTO;
import ru.mudan.dto.schedule.ScheduleUpdateDTO;
import ru.mudan.services.auth.AuthService;
import ru.mudan.services.schedule.ScheduleService;

/**
 * Контроллер, принимающий запросы
 * для работы с ячейками расписания
 */
@Controller
@RequestMapping("/schedules")
@RequiredArgsConstructor
@SuppressWarnings("MultipleStringLiterals")
public class ScheduleController {

    private final ScheduleService scheduleService;
    private final AuthService authService;

    /**
     * Эндпоинт для получения всех ячеек расписания для класса
     *
     * @param classId - id класса
     */
    @GetMapping("/all/{classId}")
    public String getPageWithInfoAboutAllSchedulesForClass(
            @PathVariable("classId") Long classId,
            Model model,
            Authentication authentication) {
        authService.hasRoleAdminOrStudentFromClassOrParentThatHasStudentInClass(classId, authentication);
        model.addAttribute("schedules", scheduleService.findAllSchedulesForClass(classId));
        return "schedule/schedule-class-index";
    }

    /**
     * Эндпоинт для получения ячейки расписания по id
     *
     * @param id - id ячейки расписания
     */
    @GetMapping("/{id}")
    public String getPageWithInfoAboutScheduleById(
            @PathVariable("id") Long id,
            Model model,
            Authentication authentication) {
        authService.hasRoleAdmin(authentication);
        model.addAttribute("schedule", scheduleService.findById(id));
        return "schedule/schedule-class-show";
    }

    /**
     * Эндпоинт для создания ячейки расписания
     *
     * @param scheduleCreateDTO - входные данные для создания ячейки расписания
     */
    @PostMapping
    public String addScheduleForClass(@Valid ScheduleCreateDTO scheduleCreateDTO,
                                      Authentication authentication) {
        authService.hasRoleAdmin(authentication);
        scheduleService.save(scheduleCreateDTO);
        return "redirect:schedules/all/" + scheduleCreateDTO.classId();
    }

    /**
     * Эндпоинт для получения шаблона для редактирования ячейки расписания
     *
     * @param id - id ячейки расписания
     */
    @GetMapping("/{id}/edit")
    public String getPageForEditingSchedule(@PathVariable("id") Long id,
                                            Model model,
                                            Authentication authentication) {
        authService.hasRoleAdmin(authentication);
        model.addAttribute("schedule", scheduleService.findById(id));
        return "schedule/schedule-class-edit";
    }

    /**
     * Эндпоинт для обновления ячейки расписания
     *
     * @param id                - id ячейки расписания
     * @param scheduleUpdateDTO - входные данные для обновления ячейки расписания
     */
    @PutMapping("/{id}")
    public String updateScheduleForClass(@Valid ScheduleUpdateDTO scheduleUpdateDTO,
                                         @PathVariable("id") Long id,
                                         Authentication authentication) {
        authService.hasRoleAdmin(authentication);
        scheduleService.update(scheduleUpdateDTO, id);
        return "redirect:/schedules/" + id;
    }

    /**
     * Эндпоинт для удаления ячейки расписания по id
     *
     * @param id - id ячейки расписания
     */
    @DeleteMapping("/{id}")
    public String deleteSchedule(@PathVariable("id") Long id,
                                 Authentication authentication) {
        authService.hasRoleAdmin(authentication);
        scheduleService.deleteById(id);
        return "redirect:/classes/all";
    }
}
