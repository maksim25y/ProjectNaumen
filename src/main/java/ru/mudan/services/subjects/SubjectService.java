package ru.mudan.services.subjects;

import java.util.List;
import java.util.NoSuchElementException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.mudan.domain.entity.Subject;
import ru.mudan.domain.repositories.ClassRepository;
import ru.mudan.domain.repositories.SubjectsRepository;
import ru.mudan.dto.subjects.SubjectCreateDTO;
import ru.mudan.dto.subjects.SubjectDTO;
import ru.mudan.dto.subjects.SubjectUpdateDTO;
import ru.mudan.exceptions.entity.already_exists.SubjectAlreadyExistsException;
import ru.mudan.exceptions.entity.not_found.ClassEntityNotFoundException;

@Service
@SuppressWarnings({"MemberName", "MagicNumber"})
@RequiredArgsConstructor
public class SubjectService {

    private final String SUBJECT_ALREADY_EXIST = "Subject already exists";
    private final String SUBJECT_NOT_FOUND = "Subject not found";
    private final SubjectsRepository subjectsRepository;
    private final ClassRepository classRepository;

    public List<SubjectDTO> findAll() {
        return subjectsRepository.findAll()
                .stream()
                .map(sb -> SubjectDTO
                        .builder()
                        .id(sb.getId())
                        .code(sb.getCode())
                        .name(sb.getName())
                        .description(sb.getDescription())
                        .type(sb.getType())
                        .build())
                .toList();
    }

    public SubjectDTO findById(Long id) {
        var foundSubject = subjectsRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException(SUBJECT_NOT_FOUND));

        return SubjectDTO
                .builder()
                .id(foundSubject.getId())
                .name(foundSubject.getName())
                .code(foundSubject.getCode())
                .description(foundSubject.getDescription())
                .type(foundSubject.getType())
                .build();
    }

    public void save(SubjectCreateDTO request) {
        var classForSubject = classRepository.findById(request.classId())
                .orElseThrow(() -> new ClassEntityNotFoundException(request.classId()));

        var codeForSb = generateCode(request.name(), classForSubject.getNumber(), classForSubject.getLetter());

        checkSubjectAlreadyExists(codeForSb);

        var subjectForSaving = new Subject(
                request.name(),
                request.type(),
                codeForSb,
                request.description());

        subjectForSaving.setClassEntity(classForSubject);

        subjectsRepository.save(subjectForSaving);
    }

    private String generateCode(String name, Integer classNumber, String letter) {
        return name.substring(0, 3).toUpperCase() + classNumber + letter;
    }

    public void update(SubjectUpdateDTO request, Long id) {
        var foundSubject = subjectsRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException(SUBJECT_NOT_FOUND));

        foundSubject.setType(request.type());
        foundSubject.setDescription(request.description());

        subjectsRepository.save(foundSubject);
    }

    public void deleteById(Long id) {
        var foundSubject = subjectsRepository
                .findById(id).orElseThrow(() -> new NoSuchElementException(SUBJECT_NOT_FOUND));
        subjectsRepository.delete(foundSubject);
    }

    private void checkSubjectAlreadyExists(String code) {
        var foundSubject = subjectsRepository.findByCode(code);

        if (foundSubject.isPresent()) {
            throw new SubjectAlreadyExistsException(SUBJECT_ALREADY_EXIST);
        }
    }
}
