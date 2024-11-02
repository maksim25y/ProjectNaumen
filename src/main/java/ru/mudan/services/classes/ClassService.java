package ru.mudan.services.classes;

import java.util.List;
import java.util.NoSuchElementException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.mudan.domain.entity.ClassEntity;
import ru.mudan.domain.repositories.ClassRepository;
import ru.mudan.dto.ClassDTO;
import ru.mudan.exceptions.ClassAlreadyExistsException;
import ru.mudan.services.CrudService;

@Service
@RequiredArgsConstructor
public class ClassService implements CrudService<ClassDTO> {

    private final ClassRepository classRepository;

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
        var foundClass = classRepository.findById(id).orElseThrow(() -> new NoSuchElementException("Class not found"));
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
        checkClassAlreadyExists(request);

        var classEntity = new ClassEntity(
                request.letter(),
                request.number(),
                request.description());

        classRepository.save(classEntity);
    }

    @Override
    public void update(ClassDTO request, Long id) {
        var foundClass = classRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Class not found"));

        checkClassAlreadyExistsAndIdNotEquals(request, id);

        foundClass.setLetter(request.letter());
        foundClass.setNumber(request.number());
        foundClass.setDescription(request.description());
        classRepository.save(foundClass);
    }

    @Override
    public void deleteById(Long id) {
        var foundClass = classRepository.findById(id);

        if(foundClass.isEmpty()) {
            throw new NoSuchElementException("Class not found");
        }

        classRepository.deleteById(id);
    }

    private void checkClassAlreadyExists(ClassDTO request) {
        var foundClass = classRepository
                .findByLetterAndNumber(
                        request.letter(),
                        request.number());

        if(foundClass.isPresent()) {
            throw new ClassAlreadyExistsException("Class already exists");
        }
    }

    private void checkClassAlreadyExistsAndIdNotEquals(ClassDTO request, Long id) {
        var foundClass = classRepository
                .findByLetterAndNumber(
                        request.letter(),
                        request.number());

        if(foundClass.isPresent()) {
            if(!foundClass.get().getId().equals(id)) {
                throw new ClassAlreadyExistsException("Class already exists");
            }
        }
    }
}
