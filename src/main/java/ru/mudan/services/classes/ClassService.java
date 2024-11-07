package ru.mudan.services.classes;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.mudan.domain.entity.ClassEntity;
import ru.mudan.domain.repositories.ClassRepository;
import ru.mudan.domain.repositories.StudentRepository;
import ru.mudan.domain.repositories.SubjectsRepository;
import ru.mudan.dto.classes.ClassDTO;
import ru.mudan.dto.student.StudentDTO;
import ru.mudan.dto.subjects.SubjectDTO;
import ru.mudan.exceptions.entity.already_exists.ClassAlreadyExistsException;
import ru.mudan.exceptions.entity.not_found.ClassEntityNotFoundException;
import ru.mudan.services.CrudService;

@Service
@SuppressWarnings("MemberName")
@RequiredArgsConstructor
@Transactional
public class ClassService implements CrudService<ClassDTO> {

    private final ClassRepository classRepository;
    private final StudentRepository studentRepository;
    private final SubjectsRepository subjectsRepository;

    @Override
    public List<ClassDTO> findAll() {
        var allClasses = classRepository.findAll();

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
        //TODO - метод через транзакции, так как если id не верный у школьника, то надо откат
        checkClassAlreadyExists(request);

        var classEntity = new ClassEntity(
                request.letter(),
                request.number(),
                request.description());

        var savedClass = classRepository.save(classEntity);

        if (request.studentsIds() != null) {
            request.studentsIds()
                    .forEach(id -> {
                        var student = studentRepository.findById(id).get();
                        student.setClassEntity(savedClass);
                        studentRepository.save(student);
                    });
        }

        if (request.subjectsIds() != null) {
            request.subjectsIds()
                    .forEach(id -> {
                    var subject = subjectsRepository.findById(id).get();
                    subject.setClassEntity(savedClass);
                    subjectsRepository.save(subject);
                    });
        }
    }

    @Override
    public void update(ClassDTO request, Long id) {
        var foundClass = findClassEntityById(id);

        checkClassAlreadyExistsAndIdNotEquals(request, id);

        foundClass.setLetter(request.letter());
        foundClass.setNumber(request.number());
        foundClass.setDescription(request.description());
        classRepository.save(foundClass);
    }

    @Override
    public void deleteById(Long id) {
        findClassEntityById(id);

        classRepository.deleteById(id);
    }

    public List<StudentDTO> findAllStudentsForClass(ClassDTO request) {
        var foundClass = findClassEntityById(request.id());

        return foundClass.getStudents()
                .stream()
                .map(st -> StudentDTO
                        .builder()
                        .id(st.getId())
                        .firstname(st.getFirstname())
                        .lastname(st.getLastname())
                        .patronymic(st.getPatronymic())
                        .email(st.getEmail())
                        .build())
                .toList();
    }

    public List<SubjectDTO> findAllSubjectsForClass(ClassDTO request) {
        var foundClass = findClassEntityById(request.id());

        return foundClass.getSubjects()
                .stream()
                .map(sb -> SubjectDTO
                        .builder()
                        .id(sb.getId())
                        .code(sb.getCode())
                        .type(sb.getType())
                        .name(sb.getName())
                        .build())
                .toList();
    }

    public List<StudentDTO> findStudentsWithNotClass() {
        return studentRepository.findAllByClassEntity(null)
                .stream()
                .map(st -> StudentDTO
                        .builder()
                        .id(st.getId())
                        .firstname(st.getFirstname())
                        .lastname(st.getLastname())
                        .patronymic(st.getPatronymic())
                        .email(st.getEmail())
                        .build())
                .toList();
    }

    public List<SubjectDTO> findSubjectsWithNotClass() {
        return subjectsRepository.findAllByClassEntity(null)
                .stream()
                .map(sb -> SubjectDTO
                        .builder()
                        .id(sb.getId())
                        .code(sb.getCode())
                        .type(sb.getType())
                        .name(sb.getName())
                        .build())
                .toList();
    }

    private void checkClassAlreadyExists(ClassDTO request) {
        var foundClass = classRepository
                .findByLetterAndNumber(
                        request.letter(),
                        request.number());

        if (foundClass.isPresent()) {
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
                throw new ClassAlreadyExistsException(request.number(), request.letter());
            }
        }
    }

    public void addStudentsToClass(Long classId, List<Long> studentsForAddingIds) {
        var foundClass = findClassEntityById(classId);

        if (studentsForAddingIds != null) {
            studentsForAddingIds
                    .forEach(id -> {
                        var student = studentRepository.findById(id).get();
                        student.setClassEntity(foundClass);
                        studentRepository.save(student);
                    });
        }
    }

    public void addSubjectsToClass(Long classId, List<Long> subjectsForAddingIds) {
        var foundClass = findClassEntityById(classId);

        if (subjectsForAddingIds != null) {
            subjectsForAddingIds
                    .forEach(id -> {
                        var subject = subjectsRepository.findById(id).get();
                        subject.setClassEntity(foundClass);
                        subjectsRepository.save(subject);
                    });
        }
    }

    private ClassEntity findClassEntityById(Long classId) {
        return classRepository.findById(classId)
                .orElseThrow(() -> new ClassEntityNotFoundException(classId));
    }
}
