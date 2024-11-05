package ru.mudan.services.schedule;

import java.util.Comparator;
import java.util.List;
import java.util.NoSuchElementException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.mudan.domain.entity.Schedule;
import ru.mudan.domain.repositories.ClassRepository;
import ru.mudan.domain.repositories.ScheduleRepository;
import ru.mudan.dto.schedule.ScheduleDTO;
import ru.mudan.util.ScheduleUtil;

@Service
@RequiredArgsConstructor
public class ScheduleService {

    private final ScheduleRepository scheduleRepository;
    private final ClassRepository classRepository;


    public List<ScheduleDTO> findAllSchedulesForClass(Long classId) {
        var foundClass = classRepository.findById(classId)
                .orElseThrow(() -> new NoSuchElementException("Class not found"));
        var listOfSchedules = foundClass.getSchedules();
        listOfSchedules.sort(Comparator.comparing(Schedule::getDayOfWeek).thenComparing(Schedule::getStartTime));

        return listOfSchedules
                .stream()
                .map(sch -> ScheduleDTO
                        .builder()
                        .numberOfClassRoom(sch.getNumberOfClassroom())
                        .dayOfWeek(ScheduleUtil.days.get(sch.getDayOfWeek()))
                        .subjectName(sch.getSubject().getName())
                        .startTime(sch.getStartTime())
                        .build())
                .toList();
    }

    public ScheduleDTO findById(Long id) {
        return null;
    }

    public void save(ScheduleDTO request) {

    }

    public void update(ScheduleDTO request, Long id) {

    }

    public void deleteById(Long id) {

    }
}
