package ru.mudan.services.classes;

import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.mudan.domain.entity.ClassEntity;
import ru.mudan.domain.repositories.ClassRepository;
import ru.mudan.domain.repositories.StudentRepository;
import ru.mudan.domain.repositories.SubjectsRepository;
import ru.mudan.dto.classes.ClassDTO;
import ru.mudan.exceptions.entity.already_exists.ClassAlreadyExistsException;
import ru.mudan.exceptions.entity.not_found.ClassEntityNotFoundException;
import ru.mudan.exceptions.entity.not_found.StudentNotFoundException;
import ru.mudan.exceptions.entity.not_found.SubjectNotFoundException;
import ru.mudan.services.CrudService;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class ClassService implements CrudService<ClassDTO> {

    @Value("${size.of.code}")
    private Integer sizeOfPartFromSubjectNameForSubjectCode;
    private final ClassRepository classRepository;
    private final StudentRepository studentRepository;
    private final SubjectsRepository subjectsRepository;

    @Override
    public List<ClassDTO> findAll() {
        log.info("Started getting all classes");
        var allClasses = classRepository.findAll();
        log.info("Finished getting all classes");

        return allClasses.stream()
                .map(cl -> ClassDTO
                        .builder()
                        .id(cl.getId())
                        .number(cl.getNumber())
                        .letter(cl.getLetter())
                        .description(cl.getDescription())
                        .build()).toList();
    }

    @Override
    public ClassDTO findById(Long id) {
        var foundClass = findClassEntityById(id);

        return ClassDTO
                .builder()
                .id(foundClass.getId())
                .number(foundClass.getNumber())
                .letter(foundClass.getLetter())
                .description(foundClass.getDescription())
                .build();
    }

    @Override
    public void save(ClassDTO request) {
        log.info("Started creating new class {}{}", request.number(), request.letter());
        checkClassAlreadyExists(request);

        var classEntity = new ClassEntity(
                request.letter(),
                request.number(),
                request.description());


        var savedClassEntity = classRepository.save(classEntity);

        if (request.studentsIds() != null) {
            request.studentsIds().forEach(id -> {
                studentRepository.findById(id).ifPresent(st -> {
                            st.setClassEntity(savedClassEntity);
                            studentRepository.save(st);
                        }
                );
            });
        }
        log.info("Finished creating new class {}{}", request.number(), request.letter());
    }

    @Override
    public void update(ClassDTO request, Long id) {
        log.info("Started updating class with id={}", id);
        var foundClass = findClassEntityById(id);

        checkClassAlreadyExistsAndIdNotEquals(request, id);

        foundClass.setLetter(request.letter());
        foundClass.setNumber(request.number());
        foundClass.setDescription(request.description());

        var subjectsForClass = foundClass.getSubjects();

        subjectsForClass.forEach(sb -> {
            var code = sb.getCode().substring(0,
                    sizeOfPartFromSubjectNameForSubjectCode)
                    + foundClass.getNumber()
                    + foundClass.getLetter();
            sb.setCode(code);
            subjectsRepository.save(sb);
        });

        classRepository.save(foundClass);
        log.info("Finished updating class with id={}", id);
    }

    @Override
    public void deleteById(Long id) {
        log.info("Started deleting class with id={}", id);
        findClassEntityById(id);

        classRepository.deleteById(id);
        log.info("Finished deleting class with id={}", id);
    }

    private void checkClassAlreadyExists(ClassDTO request) {
        var foundClass = classRepository
                .findByLetterAndNumber(
                        request.letter(),
                        request.number());

        if (foundClass.isPresent()) {
            log.info("Class {}{} already exists", request.number(), request.letter());
            throw new ClassAlreadyExistsException(request.number(), request.letter());
        }
    }

    private void checkClassAlreadyExistsAndIdNotEquals(ClassDTO request, Long id) {
        var foundClass = classRepository
                .findByLetterAndNumber(
                        request.letter(),
                        request.number());

        if (foundClass.isPresent()) {
            if (!foundClass.get().getId().equals(id)) {
                log.info("Class with parameters {}{} for updated already exists", request.number(), request.letter());
                throw new ClassAlreadyExistsException(request.number(), request.letter());
            }
        }
    }

    public void addStudentsToClass(Long classId, List<Long> studentsForAddingIds) {
        log.info("Started adding students to class with id={}", classId);
        var foundClass = findClassEntityById(classId);

        if (studentsForAddingIds != null) {
            studentsForAddingIds
                    .forEach(id -> {
                        var student = studentRepository.findById(id)
                                .orElseThrow(() -> new StudentNotFoundException(id));
                        student.setClassEntity(foundClass);
                        studentRepository.save(student);
                    });
            log.info("Finished adding students to class with id={}", classId);
        }
    }

    public void addSubjectsToClass(Long classId, List<Long> subjectsForAddingIds) {
        log.info("Started adding subjects to class with id={}", classId);
        var foundClass = findClassEntityById(classId);

        if (subjectsForAddingIds != null) {
            subjectsForAddingIds
                    .forEach(id -> {
                        var subject = subjectsRepository.findById(id)
                                .orElseThrow(() -> new SubjectNotFoundException(id));
                        subject.setClassEntity(foundClass);
                        subjectsRepository.save(subject);
                    });
            log.info("Finished adding subjects to class with id={}", classId);
        }
    }

    private ClassEntity findClassEntityById(Long classId) {
        return classRepository.findById(classId)
                .orElseThrow(() -> new ClassEntityNotFoundException(classId));
    }
}
