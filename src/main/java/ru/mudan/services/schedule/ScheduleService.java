package ru.mudan.services.schedule;

import java.util.Comparator;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
import ru.mudan.facade.schedule.ScheduleFacade;

/**
 * Класс с описанием бизнес-логики
 * для работы с сущностью Schedule
 */
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class ScheduleService {

    private final ScheduleRepository scheduleRepository;
    private final ClassRepository classRepository;
    private final SubjectsRepository subjectsRepository;
    private final ScheduleFacade scheduleFacade;

    /**
     * Метод для получения списка ячеек расписания класса
     *
     * @param classId - id класса
     */
    public List<ScheduleDTO> findAllSchedulesForClass(Long classId) {
        log.info("Started getting all schedules for class with id={}", classId);
        var foundClass = classRepository.findById(classId)
                .orElseThrow(() -> new ClassEntityNotFoundException(classId));
        var listOfSchedules = foundClass.getSchedules();
        listOfSchedules.sort(Comparator.comparing(Schedule::getDayOfWeek)
                .thenComparing(Schedule::getStartTime));
        log.info("Finished getting all schedules for class with id={}", classId);

        return listOfSchedules
                .stream()
                .map(scheduleFacade::convertEntityToDTO)
                .toList();
    }

    /**
     * Метод для получения ячейки расписания по id
     *
     * @param id - id ячейки расписания
     */
    public ScheduleDTO findById(Long id) {
        var foundSchedule = scheduleRepository.findById(id)
                .orElseThrow(() -> new ScheduleNotFoundException(id));

        return scheduleFacade.convertEntityToDTO(foundSchedule);
    }

    /**
     * Метод для получения сохранения ячейки расписания
     *
     * @param request - входные данные
     */
    public void save(ScheduleCreateDTO request) {
        log.info("Started creating schedule for class "
                        + "with id={} and subject with id={}",
                request.classId(),
                request.subjectId());
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
        log.info("Finished creating schedule for class with id={} "
                        + "and subject with id={}",
                request.classId(),
                request.subjectId());
    }

    /**
     * Метод для создания обновления существующей ячейки расписания по id
     *
     * @param request - входные данные для обновления
     * @param id      - id ячейки расписания для обновления
     */
    public void update(ScheduleUpdateDTO request, Long id) {
        log.info("Started updating schedule with id={}", id);
        var foundSchedule = scheduleRepository.findById(id)
                .orElseThrow(() -> new ScheduleNotFoundException(id));

        foundSchedule.setDayOfWeek(request.dayOfWeek());
        foundSchedule.setStartTime(request.startTime());
        foundSchedule.setNumberOfClassroom(request.numberOfClassroom());
        scheduleRepository.save(foundSchedule);
        log.info("Finished updating schedule with id={}", id);
    }

    /**
     * Метод для удаления ячейки расписания по id
     *
     * @param id - id ячейки расписания для удаления
     */
    public void deleteById(Long id) {
        log.info("Started deleting schedule with id={}", id);
        var foundSchedule = scheduleRepository.findById(id)
                .orElseThrow(() -> new ScheduleNotFoundException(id));

        scheduleRepository.delete(foundSchedule);
        log.info("Finished deleting schedule with id={}", id);
    }

    /**
     * Метод для получения ячеек расписания для класса
     *
     * @param subjectId - id класса
     */
    public List<ScheduleDTO> findAllBySubjectId(Long subjectId) {
        log.info("Started getting all schedules for subject with id={}", subjectId);
        var foundSubject = subjectsRepository.findById(subjectId)
                .orElseThrow(() -> new SubjectNotFoundException(subjectId));

        var teacherSchedule = foundSubject.getSchedules();
        teacherSchedule.sort((Comparator.comparing(Schedule::getDayOfWeek)
                .thenComparing(Schedule::getStartTime)));
        log.info("Finished getting all schedules for subject with id={}", subjectId);

        return teacherSchedule.stream()
                .map(scheduleFacade::convertEntityToDTO)
                .toList();
    }
}
