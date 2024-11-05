package ru.mudan.services.classes;

import java.util.List;
import java.util.NoSuchElementException;
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
import ru.mudan.exceptions.ClassAlreadyExistsException;
import ru.mudan.services.CrudService;

@Service
@SuppressWarnings("MemberName")
@RequiredArgsConstructor
@Transactional
public class ClassService implements CrudService<ClassDTO> {

    private final String CLASS_ALREADY_EXIST = "Class already exists";
    private final String CLASS_NOT_FOUND = "Class not found";

    private final ClassRepository classRepository;
    private final StudentRepository studentRepository;
    private final SubjectsRepository subjectsRepository;

    @Override
    public List<ClassDTO> findAll() {
        var allClasses = classRepository.findAll();

        return allClasses.stream().map(cl -> ClassDTO
                .builder()
                .id(cl.getId())
                .number(cl.getNumber())
                .letter(cl.getLetter())
                .description(cl.getDescription())
                .build()).toList();
    }

    @Override
    public ClassDTO findById(Long id) {
        var foundClass = getClassEntity(id);

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
        var foundClass = getClassEntity(id);

        checkClassAlreadyExistsAndIdNotEquals(request, id);

        foundClass.setLetter(request.letter());
        foundClass.setNumber(request.number());
        foundClass.setDescription(request.description());
        classRepository.save(foundClass);
    }

    @Override
    public void deleteById(Long id) {
        var foundClass = classRepository.findById(id);

        if (foundClass.isEmpty()) {
            throw new NoSuchElementException(CLASS_NOT_FOUND);
        }

        classRepository.deleteById(id);
    }

    public List<StudentDTO> findAllStudentsForClass(ClassDTO request) {
        var foundClass = getClassEntity(request.id());

        return foundClass.getStudents()
                .stream()
                .map(st -> StudentDTO
                        .builder()
                        .firstname(st.getFirstname())
                        .lastname(st.getLastname())
                        .patronymic(st.getPatronymic())
                        .email(st.getEmail())
                        .build())
                .toList();
    }

    public List<SubjectDTO> findAllSubjectsForClass(ClassDTO request) {
        var foundClass = getClassEntity(request.id());

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
            throw new ClassAlreadyExistsException(CLASS_ALREADY_EXIST);
        }
    }

    private void checkClassAlreadyExistsAndIdNotEquals(ClassDTO request, Long id) {
        var foundClass = classRepository
                .findByLetterAndNumber(
                        request.letter(),
                        request.number());

        if (foundClass.isPresent()) {
            if (!foundClass.get().getId().equals(id)) {
                throw new ClassAlreadyExistsException(CLASS_ALREADY_EXIST);
            }
        }
    }

    public void addStudentsToClass(Long classId, List<Long> studentsForAddingIds) {
        var foundClass = getClassEntity(classId);

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
        var foundClass = getClassEntity(classId);

        if (subjectsForAddingIds != null) {
            subjectsForAddingIds
                    .forEach(id -> {
                        var subject = subjectsRepository.findById(id).get();
                        subject.setClassEntity(foundClass);
                        subjectsRepository.save(subject);
                    });
        }
    }

    private ClassEntity getClassEntity(Long classId) {
        return classRepository.findById(classId)
                .orElseThrow(() -> new NoSuchElementException(CLASS_NOT_FOUND));
    }
}
