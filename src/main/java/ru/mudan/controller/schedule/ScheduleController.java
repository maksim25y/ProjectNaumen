package ru.mudan.controller.schedule;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import ru.mudan.dto.schedule.ScheduleCreateDTO;
import ru.mudan.dto.schedule.ScheduleUpdateDTO;
import ru.mudan.services.schedule.ScheduleService;

@Controller
@RequestMapping("/schedules")
@RequiredArgsConstructor
@SuppressWarnings({"MultipleStringLiterals", "MemberName"})
public class ScheduleController {

    private final String REDIRECT_CLASSES_ALL = "redirect:/classes/all";
    private final ScheduleService scheduleService;

    @GetMapping("/all/{classId}")
    public String getPageWithInfoAboutAllSchedulesForClass(
            @PathVariable("classId") Long classId,
            Model model) {
        model.addAttribute("schedules", scheduleService.findAllSchedulesForClass(classId));
        return "schedule/schedule-class-index";
    }

    @GetMapping("/{id}")
    public String getPageWithInfoAboutScheduleById(
            @PathVariable("id") Long id,
            Model model) {
        model.addAttribute("schedule", scheduleService.findById(id));
        return "schedule/schedule-class-show";
    }

    @PostMapping
    public String addScheduleForClass(@Valid ScheduleCreateDTO request) {
        scheduleService.save(request);
        return REDIRECT_CLASSES_ALL;
    }

    @GetMapping("/{id}/edit")
    public String getPageForEditingSchedule(@PathVariable("id") Long id,
                       Model model) {
        model.addAttribute("schedule", scheduleService.findById(id));
        return "schedule/schedule-class-edit";
    }

    @PutMapping("/{id}")
    public String updateScheduleForClass(@Valid ScheduleUpdateDTO request,
                                         @PathVariable("id") Long id) {
        scheduleService.update(request, id);
        return "redirect:/schedules/" + id;
    }

    @DeleteMapping("/{id}")
    public String deleteSchedule(@PathVariable("id") Long id) {
        scheduleService.deleteById(id);
        return REDIRECT_CLASSES_ALL;
    }
}
