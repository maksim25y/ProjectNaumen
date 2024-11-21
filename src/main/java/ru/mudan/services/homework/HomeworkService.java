package ru.mudan.services.homework;

import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.mudan.domain.entity.Homework;
import ru.mudan.domain.repositories.ClassRepository;
import ru.mudan.domain.repositories.HomeworkRepository;
import ru.mudan.domain.repositories.SubjectsRepository;
import ru.mudan.dto.homework.HomeworkCreateDTO;
import ru.mudan.dto.homework.HomeworkDTO;
import ru.mudan.exceptions.entity.not_found.ClassEntityNotFoundException;
import ru.mudan.exceptions.entity.not_found.HomeworkNotFoundException;
import ru.mudan.exceptions.entity.not_found.SubjectNotFoundException;
import ru.mudan.facade.homework.HomeworkFacade;

/**
 * Класс с описанием бизнес-логики
 * для работы с сущностью Homework
 */
@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class HomeworkService {

    private final HomeworkRepository homeworkRepository;
    private final ClassRepository classRepository;
    private final SubjectsRepository subjectsRepository;
    private final HomeworkFacade homeworkFacade;

    /**
     * Метод для получения списка ДЗ класса
     *
     * @param classId - id класса
     * @param subjectId - id предмета
     */
    public List<HomeworkDTO> findAllByClassAndSubject(Long classId, Long subjectId) {
        log.info("Started getting all homeworks for subject with id={} and class with id={}", subjectId, classId);
        var foundClass = classRepository.findById(classId)
                .orElseThrow(() -> new ClassEntityNotFoundException(classId));
        var foundSubject = subjectsRepository.findById(subjectId)
                .orElseThrow(() -> new SubjectNotFoundException(subjectId));

        var listOfHomework = homeworkRepository.findByClassEntityAndSubject(foundClass, foundSubject);
        log.info("Finished getting all homeworks for subject with id={} and class with id={}", subjectId, classId);
        return listOfHomework
                .stream()
                .map(homeworkFacade::convertEntityToDTO)
                .toList();
    }

    /**
     * Метод для получения сохранения ДЗ
     *
     * @param hwDTO - входные данные
     */
    public void save(HomeworkCreateDTO hwDTO) {
        var foundSubject = subjectsRepository.findById(hwDTO.subjectId())
                .orElseThrow(() -> new SubjectNotFoundException(hwDTO.subjectId()));

        var foundClass = foundSubject.getClassEntity();

        var homework = new Homework(hwDTO.title(), hwDTO.description(), hwDTO.deadline());
        homework.setClassEntity(foundClass);
        homework.setSubject(foundSubject);

        homeworkRepository.save(homework);
    }

    /**
     * Метод для получения списка ДЗ по классу
     */
    public List<HomeworkDTO> findAllByClass(Long classId) {
        log.info("Started getting all homeworks for class with id={}", classId);
        var foundClass = classRepository.findById(classId)
                .orElseThrow(() -> new ClassEntityNotFoundException(classId));
        var homeworksForClass = foundClass.getHomeworks();
        log.info("Finished getting all homeworks for class with id={}", classId);

        return homeworksForClass.stream()
                .map(homeworkFacade::convertEntityToDTO)
                .toList();
    }

    /**
     * Метод для получения ДЗ по id
     *
     * @param id - id ДЗ
     */
    public HomeworkDTO findById(Long id) {
        var foundHomework = homeworkRepository.findById(id)
                .orElseThrow(() -> new HomeworkNotFoundException(id));

        return homeworkFacade.convertEntityToDTO(foundHomework);
    }

    /**
     * Метод для удаления ДЗ по id
     *
     * @param id - id ДЗ для удаления
     */
    public void delete(Long id) {
        log.info("Started deleting homework for with id={}", id);
        var foundHomework = homeworkRepository.findById(id)
                .orElseThrow(() -> new HomeworkNotFoundException(id));
        homeworkRepository.delete(foundHomework);
        log.info("Finished deleting homework for with id={}", id);
    }

    /**
     * Метод для получения списка ДЗ по предмету
     */
    public List<HomeworkDTO> findAllBySubject(Long subjectId) {
        log.info("Started getting all homeworks for subject with id={}", subjectId);
        var foundSubject = subjectsRepository.findById(subjectId)
                .orElseThrow(() -> new SubjectNotFoundException(subjectId));

        var homeworksForSubject = foundSubject.getHomeworks();
        log.info("Finished getting all homeworks for subject with id={}", subjectId);

        return homeworksForSubject.stream()
                .map(homeworkFacade::convertEntityToDTO)
                .toList();


    }

    /**
     * Метод для обновления существующего ДЗ по id
     *
     * @param homeworkDTO - входные данные для обновления
     * @param id      - id ДЗ для обновления
     */
    public void update(Long id, HomeworkDTO homeworkDTO) {
        var foundHomework = homeworkRepository.findById(id)
                .orElseThrow(() -> new HomeworkNotFoundException(id));

        foundHomework.setTitle(homeworkDTO.title());
        foundHomework.setDescription(homeworkDTO.description());
        foundHomework.setDeadline(homeworkDTO.deadline());
        homeworkRepository.save(foundHomework);
    }
}
