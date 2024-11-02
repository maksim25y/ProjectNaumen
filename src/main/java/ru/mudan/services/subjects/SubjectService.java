package ru.mudan.services.subjects;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.mudan.domain.repositories.SubjectsRepository;
import ru.mudan.dto.subjects.SubjectDTO;
import ru.mudan.services.CrudService;

import java.util.List;

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
                        .name(sb.getName())
                        .description(sb.getDescription())
                        .type(sb.getType())
                        .build())
                .toList();
    }

    @Override
    public SubjectDTO findById(Long id) {
        return null;
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
