package ru.mudan.services.classes;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.mudan.domain.repositories.ClassRepository;
import ru.mudan.dto.ClassResponseDTO;

@Service
@RequiredArgsConstructor
public class ClassService {

    private final ClassRepository classRepository;

    public List<ClassResponseDTO> findAllClasses() {
        var allClasses = classRepository.findAll();

        return allClasses.stream().map(cl -> ClassResponseDTO
                .builder()
                .id(cl.getId())
                .number(cl.getNumber())
                .letter(cl.getLetter())
                .description(cl.getDescription())
                .build()).toList();
    }
}
