package ru.mudan.services.schedule;

import java.util.Comparator;
import java.util.List;
import java.util.NoSuchElementException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.mudan.domain.entity.Schedule;
import ru.mudan.domain.repositories.ClassRepository;
import ru.mudan.domain.repositories.ScheduleRepository;
import ru.mudan.dto.schedule.ScheduleDTOResponse;
import ru.mudan.util.ScheduleUtil;

@Service
@RequiredArgsConstructor
public class ScheduleService {

    private final ScheduleRepository scheduleRepository;
    private final ClassRepository classRepository;


    public List<ScheduleDTOResponse> findAllSchedulesForClass(Long classId) {
        var foundClass = classRepository.findById(classId)
                .orElseThrow(() -> new NoSuchElementException("Class not found"));
        var listOfSchedules = foundClass.getSchedules();
        listOfSchedules.sort(Comparator.comparing(Schedule::getDayOfWeek).thenComparing(Schedule::getStartTime));

        return listOfSchedules
                .stream()
                .map(sch -> ScheduleDTOResponse
                        .builder()
                        .id(sch.getId())
                        .numberOfClassRoom(sch.getNumberOfClassroom())
                        .dayOfWeek(ScheduleUtil.days.get(sch.getDayOfWeek()))
                        .subjectName(sch.getSubject().getName())
                        .startTime(sch.getStartTime())
                        .build())
                .toList();
    }

    public ScheduleDTOResponse findById(Long id) {
        var foundSchedule = scheduleRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Schedule not found"));

        return ScheduleDTOResponse
                .builder()
                .id(foundSchedule.getId())
                .numberOfClassRoom(foundSchedule.getNumberOfClassroom())
                .dayOfWeek(ScheduleUtil.days.get(foundSchedule.getDayOfWeek()))
                .subjectName(foundSchedule.getSubject().getName())
                .startTime(foundSchedule.getStartTime())
                .build();
    }

    public void save(ScheduleDTOResponse request) {

    }

    public void update(ScheduleDTOResponse request, Long id) {

    }

    public void deleteById(Long id) {

    }
}
