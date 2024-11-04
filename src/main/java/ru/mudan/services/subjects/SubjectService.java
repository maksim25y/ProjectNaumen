package ru.mudan.services.subjects;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.mudan.domain.entity.Subject;
import ru.mudan.domain.repositories.SubjectsRepository;
import ru.mudan.dto.subjects.SubjectDTO;
import ru.mudan.exceptions.SubjectAlreadyExistsException;
import ru.mudan.services.CrudService;

@Service
@SuppressWarnings("MemberName")
@RequiredArgsConstructor
public class SubjectService implements CrudService<SubjectDTO> {

    private final String SUBJECT_ALREADY_EXIST = "Subject already exists";
    private final String SUBJECT_NOT_FOUND = "Subject not found";
    private final SubjectsRepository subjectsRepository;

    @Override
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

    @Override
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

    @Override
    public void save(SubjectDTO request) {
        checkSubjectAlreadyExists(request.code());

        var subjectForSaving = new Subject(
                request.name(),
                request.type(),
                request.code(),
                request.description());

        subjectsRepository.save(subjectForSaving);
    }

    @Override
    public void update(SubjectDTO request, Long id) {
        var foundSubject = subjectsRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException(SUBJECT_NOT_FOUND));

        checkClassAlreadyExistsAndIdNotEquals(request.code(), id);

        foundSubject.setName(request.name());
        foundSubject.setType(request.type());
        foundSubject.setCode(request.code());
        foundSubject.setDescription(request.description());

        subjectsRepository.save(foundSubject);
    }

    @Override
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

    private void checkClassAlreadyExistsAndIdNotEquals(String code, Long id) {
        var foundSubject = subjectsRepository.findByCode(code);

        if (foundSubject.isPresent()) {
            if (!Objects.equals(foundSubject.get().getId(), id)) {
                throw new SubjectAlreadyExistsException(SUBJECT_ALREADY_EXIST);
            }
        }
    }
}
