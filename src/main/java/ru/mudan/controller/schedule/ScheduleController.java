package ru.mudan.controller.schedule;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import ru.mudan.dto.schedule.ScheduleDTORequest;
import ru.mudan.services.schedule.ScheduleService;

@Controller
@RequestMapping("/schedules")
@RequiredArgsConstructor
public class ScheduleController {

    private final ScheduleService scheduleService;

    @GetMapping("/all/{classId}")
    public String all(@PathVariable("classId") Long classId, Model model) {
        model.addAttribute("schedules", scheduleService.findAllSchedulesForClass(classId));
        return "schedule/schedule-class-index";
    }

    @GetMapping("/{id}")
    public String findById(@PathVariable("id") Long id, Model model) {
        model.addAttribute("schedule", scheduleService.findById(id));
        return "schedule/schedule-class-show";
    }

    @PostMapping
    public String addScheduleForClass(ScheduleDTORequest request) {
        scheduleService.save(request);
        return "redirect:/classes/all";
    }
}
