package ru.mudan.services.schedule;

import java.util.Comparator;
import java.util.List;
import java.util.NoSuchElementException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.mudan.domain.entity.Schedule;
import ru.mudan.domain.repositories.ClassRepository;
import ru.mudan.domain.repositories.ScheduleRepository;
import ru.mudan.domain.repositories.SubjectsRepository;
import ru.mudan.dto.schedule.ScheduleDTORequest;
import ru.mudan.dto.schedule.ScheduleDTOResponse;
import ru.mudan.util.ScheduleUtil;

@Service
@RequiredArgsConstructor
@SuppressWarnings("MemberName")
public class ScheduleService {

    private final String CLASS_NOT_FOUND = "Class not found";
    private final String SUBJECT_NOT_FOUND = "Subject not found";
    private final String SCHEDULE_NOT_FOUND = "Schedule not found";
    private final ScheduleRepository scheduleRepository;
    private final ClassRepository classRepository;
    private final SubjectsRepository subjectsRepository;


    public List<ScheduleDTOResponse> findAllSchedulesForClass(Long classId) {
        var foundClass = classRepository.findById(classId)
                .orElseThrow(() -> new NoSuchElementException(CLASS_NOT_FOUND));
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
                .orElseThrow(() -> new NoSuchElementException(SCHEDULE_NOT_FOUND));

        return ScheduleDTOResponse
                .builder()
                .id(foundSchedule.getId())
                .numberOfClassRoom(foundSchedule.getNumberOfClassroom())
                .dayOfWeek(ScheduleUtil.days.get(foundSchedule.getDayOfWeek()))
                .subjectName(foundSchedule.getSubject().getName())
                .startTime(foundSchedule.getStartTime())
                .build();
    }

    public void save(ScheduleDTORequest request) {
        var subjectForSchedule = subjectsRepository.findById(request.subjectId())
                .orElseThrow(() -> new NoSuchElementException(SUBJECT_NOT_FOUND));
        var classForSchedule = classRepository.findById(request.classId())
                .orElseThrow(() -> new NoSuchElementException(CLASS_NOT_FOUND));

        var schedule = new Schedule(
                request.dayOfWeek(),
                request.startTime(),
                request.numberOfClassroom());

        schedule.setClassEntity(classForSchedule);
        schedule.setSubject(subjectForSchedule);

        scheduleRepository.save(schedule);
    }

    public void update(ScheduleDTORequest request, Long id) {

    }

    public void deleteById(Long id) {

    }
}
