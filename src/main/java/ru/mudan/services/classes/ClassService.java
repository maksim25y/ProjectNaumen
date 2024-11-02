package ru.mudan.services.classes;

import java.util.List;
import java.util.NoSuchElementException;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.mudan.domain.repositories.ClassRepository;
import ru.mudan.dto.ClassResponseDTO;
import ru.mudan.services.CrudService;

@Service
@RequiredArgsConstructor
public class ClassService implements CrudService<ClassResponseDTO> {

    private final ClassRepository classRepository;

    @Override
    public List<ClassResponseDTO> findAll() {
        var allClasses = classRepository.findAll();

        return allClasses.stream().map(cl -> ClassResponseDTO
                .builder()
                .id(cl.getId())
                .number(cl.getNumber())
                .letter(cl.getLetter())
                .description(cl.getDescription())
                .build()).toList();
    }

    @Override
    public ClassResponseDTO findById(Long id) {
        var foundClass = classRepository.findById(id).orElseThrow(() -> new NoSuchElementException("Class not found"));
        return ClassResponseDTO
                .builder()
                .id(foundClass.getId())
                .number(foundClass.getNumber())
                .letter(foundClass.getLetter())
                .description(foundClass.getDescription())
                .build();
    }
}
