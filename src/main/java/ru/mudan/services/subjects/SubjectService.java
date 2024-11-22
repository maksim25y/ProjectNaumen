package ru.mudan.services.subjects;

import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.mudan.domain.entity.Subject;
import ru.mudan.domain.repositories.ClassRepository;
import ru.mudan.domain.repositories.SubjectsRepository;
import ru.mudan.domain.repositories.TeacherRepository;
import ru.mudan.dto.subjects.SubjectCreateDTO;
import ru.mudan.dto.subjects.SubjectDTO;
import ru.mudan.dto.subjects.SubjectUpdateDTO;
import ru.mudan.exceptions.entity.already_exists.SubjectAlreadyExistsException;
import ru.mudan.exceptions.entity.not_found.ClassEntityNotFoundException;
import ru.mudan.exceptions.entity.not_found.SubjectNotFoundException;
import ru.mudan.exceptions.entity.not_found.TeacherNotFoundException;
import ru.mudan.facade.subjects.SubjectFacade;

/**
 * Класс с описанием бизнес-логики
 * для работы с сущностью Subject
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class SubjectService {

    private final TeacherRepository teacherRepository;
    private final SubjectFacade subjectFacade;
    private final SubjectsRepository subjectsRepository;
    private final ClassRepository classRepository;

    /**
     * Метод для получения списка всех предметов
     */
    public List<SubjectDTO> findAll() {
        return subjectsRepository.findAll()
                .stream()
                .map(subjectFacade::convertEntityToDTO)
                .toList();
    }

    /**
     * Метод для получения предмета по id
     *
     * @param id - id предмета
     */
    public SubjectDTO findById(Long id) {
        var foundSubject = subjectsRepository.findById(id)
                .orElseThrow(() -> new SubjectNotFoundException(id));
        log.info("Found subject with id={}", id);

        return subjectFacade.convertEntityToDTO(foundSubject);
    }

    /**
     * Метод для создания нового предмета
     *
     * @param request - входные данные для создания
     */
    public void save(SubjectCreateDTO request) {
        log.info("Started creating subject with name {}", request.name());
        var classForSubject = classRepository.findById(request.classId())
                .orElseThrow(() -> new ClassEntityNotFoundException(request.classId()));
        log.info("Found class with id={}", request.classId());

        var teacherForSubject = teacherRepository.findById(request.teacherId())
                .orElseThrow(() -> new TeacherNotFoundException(request.teacherId()));
        log.info("Found teacher with id={}", request.classId());

        var codeForSb = generateCode(request.name(), classForSubject.getNumber(), classForSubject.getLetter());

        checkSubjectAlreadyExistsByCode(codeForSb);

        var subjectForSaving = new Subject(
                request.name(),
                request.type(),
                codeForSb,
                request.description());

        subjectForSaving.setClassEntity(classForSubject);
        subjectForSaving.setTeacher(teacherForSubject);

        subjectsRepository.save(subjectForSaving);
        log.info("Finished creating subject with name {}", request.name());
    }

    /**
     * Метод для генерации кода предмета на основании названия и класса
     *
     * @param name        - названия предмета
     * @param classNumber - номер класса
     * @param letter      - буква класса
     */
    private String generateCode(String name, Integer classNumber, String letter) {
        return name.toUpperCase() + classNumber + letter;
    }

    /**
     * Метод для создания обновления существующего предмета по id
     *
     * @param request - входные данные для обновления
     * @param id      - id предмета для обновления
     */
    public void update(SubjectUpdateDTO request, Long id) {
        log.info("Started updating subject with id={}", id);
        var foundSubject = subjectsRepository.findById(id)
                .orElseThrow(() -> new SubjectNotFoundException(id));

        foundSubject.setType(request.type());
        foundSubject.setDescription(request.description());

        subjectsRepository.save(foundSubject);
        log.info("Finished updating subject with id={}", id);
    }

    /**
     * Метод для удаления предмета по id
     *
     * @param id - id предмета для удаления
     */
    public void deleteById(Long id) {
        log.info("Started deleting subject with id={}", id);
        var foundSubject = subjectsRepository
                .findById(id).orElseThrow(() -> new SubjectNotFoundException(id));
        subjectsRepository.delete(foundSubject);
        log.info("Finished deleting subject with id={}", id);
    }

    /**
     * Метод для получения списка всех предметов для класса по id
     *
     * @param id - id класса
     */
    public List<SubjectDTO> findAllSubjectsForClass(Long id) {
        log.info("Started getting all subjects for class with id={}", id);
        var foundClass = classRepository.findById(id)
                .orElseThrow(() -> new ClassEntityNotFoundException(id));
        var subjectsForClass = foundClass.getSubjects();
        log.info("Finished getting all subjects for class with id={}", id);

        return subjectsForClass.stream()
                .map(subjectFacade::convertEntityToDTO)
                .toList();
    }

    /**
     * Метод для получения списка всех предметов для учителя по id
     *
     * @param teacherId - id учителя
     */
    public List<SubjectDTO> getSubjectsForTeacher(Long teacherId) {
        var foundTeacher = teacherRepository.findById(teacherId)
                .orElseThrow(() -> new TeacherNotFoundException(teacherId));

        var subjectsForTeacher = foundTeacher.getSubjects();
        log.info("Finished getting all subjects for teacher with id={}", teacherId);

        return subjectsForTeacher.stream()
                .map(subjectFacade::convertEntityToDTO)
                .toList();
    }

    /**
     * Метод для проверки существования предмета по коду
     *
     * @param code - id код предмета
     */
    private void checkSubjectAlreadyExistsByCode(String code) {
        var foundSubject = subjectsRepository.findByCode(code).orElse(null);

        if (foundSubject != null) {
            var classForSubject = foundSubject.getClassEntity();
            log.info("Subject with code {} already exists", code);
            throw new SubjectAlreadyExistsException(
                    foundSubject.getName(),
                    classForSubject.getNumber(),
                    classForSubject.getLetter());
        }
    }
}
