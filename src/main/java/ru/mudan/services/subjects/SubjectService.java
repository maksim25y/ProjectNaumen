package ru.mudan.services.subjects;

import java.util.List;
import java.util.NoSuchElementException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.mudan.domain.repositories.SubjectsRepository;
import ru.mudan.dto.subjects.SubjectDTO;
import ru.mudan.services.CrudService;

@Service
@RequiredArgsConstructor
public class SubjectService implements CrudService<SubjectDTO> {

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
        var foundSubject = subjectsRepository.findById(id).orElseThrow(NoSuchElementException::new);
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

    }

    @Override
    public void update(SubjectDTO request, Long id) {

    }

    @Override
    public void deleteById(Long id) {

    }
}
