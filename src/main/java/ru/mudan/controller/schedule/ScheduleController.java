package ru.mudan.controller.schedule;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import ru.mudan.services.schedule.ScheduleService;

@Controller
@RequestMapping("/schedules")
@RequiredArgsConstructor
public class ScheduleController {

    private final ScheduleService scheduleService;

    @GetMapping("/all/{classId}")
    public String all(@PathVariable Long classId, Model model) {
        model.addAttribute("schedules", scheduleService.findAllSchedulesForClass(classId));
        return "schedule-class-index";
    }
}
