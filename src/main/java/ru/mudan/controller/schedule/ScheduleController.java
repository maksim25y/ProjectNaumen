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

@Controller
@RequestMapping("/schedules")
@RequiredArgsConstructor
@SuppressWarnings({"MultipleStringLiterals", "MemberName"})
public class ScheduleController {

    private final String REDIRECT_CLASSES_ALL = "redirect:/classes/all";
    private final ScheduleService scheduleService;
    private final AuthService authService;

    @GetMapping("/all/{classId}")
    public String getPageWithInfoAboutAllSchedulesForClass(
            @PathVariable("classId") Long classId,
            Model model,
            Authentication authentication) {
        authService.hasRoleAdminOrStudentFromClassOrParentThatHasStudentInClass(classId, authentication);
        model.addAttribute("schedules", scheduleService.findAllSchedulesForClass(classId));
        return "schedule/schedule-class-index";
    }

    @GetMapping("/{id}")
    public String getPageWithInfoAboutScheduleById(
            @PathVariable("id") Long id,
            Model model,
            Authentication authentication) {
        authService.hasRoleAdmin(authentication);
        model.addAttribute("schedule", scheduleService.findById(id));
        return "schedule/schedule-class-show";
    }

    @PostMapping
    public String addScheduleForClass(@Valid ScheduleCreateDTO request,
                                      Authentication authentication) {
        authService.hasRoleAdmin(authentication);
        scheduleService.save(request);
        return REDIRECT_CLASSES_ALL;
    }

    @GetMapping("/{id}/edit")
    public String getPageForEditingSchedule(@PathVariable("id") Long id,
                                            Model model,
                                            Authentication authentication) {
        authService.hasRoleAdmin(authentication);
        model.addAttribute("schedule", scheduleService.findById(id));
        return "schedule/schedule-class-edit";
    }

    @PutMapping("/{id}")
    public String updateScheduleForClass(@Valid ScheduleUpdateDTO request,
                                         @PathVariable("id") Long id,
                                         Authentication authentication) {
        authService.hasRoleAdmin(authentication);
        scheduleService.update(request, id);
        return "redirect:/schedules/" + id;
    }

    @DeleteMapping("/{id}")
    public String deleteSchedule(@PathVariable("id") Long id,
                                 Authentication authentication) {
        authService.hasRoleAdmin(authentication);
        scheduleService.deleteById(id);
        return REDIRECT_CLASSES_ALL;
    }
}
