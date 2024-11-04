package ru.mudan.services.classes;

import java.util.List;
import java.util.NoSuchElementException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.mudan.domain.entity.ClassEntity;
import ru.mudan.domain.repositories.ClassRepository;
import ru.mudan.domain.repositories.StudentRepository;
import ru.mudan.dto.classes.ClassDTO;
import ru.mudan.dto.student.StudentDTO;
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
        var foundClass = classRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException(CLASS_NOT_FOUND));

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
    }

    @Override
    public void update(ClassDTO request, Long id) {
        var foundClass = classRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException(CLASS_NOT_FOUND));

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
        var foundClass = classRepository.findById(request.id())
                .orElseThrow(() -> new NoSuchElementException(CLASS_NOT_FOUND));

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
}
