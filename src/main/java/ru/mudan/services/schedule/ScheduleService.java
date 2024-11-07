package ru.mudan.services.schedule;

import java.util.Comparator;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.mudan.domain.entity.Schedule;
import ru.mudan.domain.repositories.ClassRepository;
import ru.mudan.domain.repositories.ScheduleRepository;
import ru.mudan.domain.repositories.SubjectsRepository;
import ru.mudan.dto.schedule.ScheduleCreateDTO;
import ru.mudan.dto.schedule.ScheduleDTO;
import ru.mudan.dto.schedule.ScheduleUpdateDTO;
import ru.mudan.exceptions.entity.not_found.ClassEntityNotFoundException;
import ru.mudan.exceptions.entity.not_found.ScheduleNotFoundException;
import ru.mudan.exceptions.entity.not_found.SubjectNotFoundException;
import ru.mudan.util.ScheduleUtil;

@Service
@RequiredArgsConstructor
@Transactional
public class ScheduleService {

    private final ScheduleRepository scheduleRepository;
    private final ClassRepository classRepository;
    private final SubjectsRepository subjectsRepository;

    public List<ScheduleDTO> findAllSchedulesForClass(Long classId) {
        var foundClass = classRepository.findById(classId)
                .orElseThrow(() -> new ClassEntityNotFoundException(classId));
        var listOfSchedules = foundClass.getSchedules();
        listOfSchedules.sort(Comparator.comparing(Schedule::getDayOfWeek).thenComparing(Schedule::getStartTime));

        return listOfSchedules
                .stream()
                .map(sch -> ScheduleDTO
                        .builder()
                        .id(sch.getId())
                        .numberOfClassRoom(sch.getNumberOfClassroom())
                        .dayOfWeek(ScheduleUtil.days.get(sch.getDayOfWeek()))
                        .subjectName(sch.getSubject().getName())
                        .startTime(sch.getStartTime())
                        .build())
                .toList();
    }

    public ScheduleDTO findById(Long id) {
        var foundSchedule = scheduleRepository.findById(id)
                .orElseThrow(() -> new ScheduleNotFoundException(id));

        return ScheduleDTO
                .builder()
                .id(foundSchedule.getId())
                .numberOfClassRoom(foundSchedule.getNumberOfClassroom())
                .dayOfWeek(ScheduleUtil.days.get(foundSchedule.getDayOfWeek()))
                .subjectName(foundSchedule.getSubject().getName())
                .startTime(foundSchedule.getStartTime())
                .build();
    }

    public void save(ScheduleCreateDTO request) {
        var subjectForSchedule = subjectsRepository.findById(request.subjectId())
                .orElseThrow(() -> new SubjectNotFoundException(request.subjectId()));
        var classForSchedule = classRepository.findById(request.classId())
                .orElseThrow(() -> new ClassEntityNotFoundException(request.classId()));

        var schedule = new Schedule(
                request.dayOfWeek(),
                request.startTime(),
                request.numberOfClassroom());

        schedule.setClassEntity(classForSchedule);
        schedule.setSubject(subjectForSchedule);

        scheduleRepository.save(schedule);
    }

    public void update(ScheduleUpdateDTO request, Long id) {
        var foundSchedule = scheduleRepository.findById(id)
                .orElseThrow(() -> new ScheduleNotFoundException(id));

        foundSchedule.setDayOfWeek(request.dayOfWeek());
        foundSchedule.setStartTime(request.startTime());
        foundSchedule.setNumberOfClassroom(request.numberOfClassroom());
        scheduleRepository.save(foundSchedule);
    }

    public void deleteById(Long id) {
        var foundSchedule = scheduleRepository.findById(id)
                .orElseThrow(() -> new ScheduleNotFoundException(id));

        scheduleRepository.delete(foundSchedule);
    }
}
