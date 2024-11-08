package ru.mudan.services.homework;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.mudan.domain.entity.Homework;
import ru.mudan.domain.repositories.ClassRepository;
import ru.mudan.domain.repositories.HomeworkRepository;
import ru.mudan.domain.repositories.SubjectsRepository;
import ru.mudan.dto.homework.HomeworkCreateDTO;
import ru.mudan.dto.homework.HomeworkDTO;
import ru.mudan.exceptions.entity.not_found.ClassEntityNotFoundException;
import ru.mudan.exceptions.entity.not_found.HomeworkNotFoundException;
import ru.mudan.exceptions.entity.not_found.SubjectNotFoundException;

@Service
@Transactional
@RequiredArgsConstructor
public class HomeworkService {

    private final HomeworkRepository homeworkRepository;
    private final ClassRepository classRepository;
    private final SubjectsRepository subjectsRepository;

    public List<HomeworkDTO> findAllByClassAndSubject(Long classId, Long subjectId) {
        var foundClass = classRepository.findById(classId)
                .orElseThrow(() -> new ClassEntityNotFoundException(classId));
        var foundSubject = subjectsRepository.findById(subjectId)
                .orElseThrow(() -> new SubjectNotFoundException(subjectId));

        var listOfHomework = homeworkRepository.findByClassEntityAndSubject(foundClass, foundSubject);
        return listOfHomework
                .stream()
                .map(hw -> HomeworkDTO
                        .builder()
                        .id(hw.getId())
                        .title(hw.getTitle())
                        .description(hw.getDescription())
                        .deadline(hw.getDeadline())
                        .build())
                .toList();
    }

    public void save(HomeworkCreateDTO hwDTO) {
        var foundClass = classRepository.findById(hwDTO.classId())
                .orElseThrow(() -> new ClassEntityNotFoundException(hwDTO.classId()));
        var foundSubject = subjectsRepository.findById(hwDTO.subjectId())
                .orElseThrow(() -> new SubjectNotFoundException(hwDTO.subjectId()));

        var homework = new Homework(hwDTO.title(), hwDTO.description(), hwDTO.deadline());
        homework.setClassEntity(foundClass);
        homework.setSubject(foundSubject);

        homeworkRepository.save(homework);
    }

    public HomeworkDTO findById(Long id) {
        var foundHomework = homeworkRepository.findById(id)
                .orElseThrow(() -> new HomeworkNotFoundException(id));

        return HomeworkDTO
                .builder()
                .id(foundHomework.getId())
                .title(foundHomework.getTitle())
                .description(foundHomework.getDescription())
                .deadline(foundHomework.getDeadline())
                .classId(foundHomework.getClassEntity().getId())
                .subjectId(foundHomework.getSubject().getId())
                .build();
    }

    public void delete(Long id) {
        var foundHomework = homeworkRepository.findById(id)
                .orElseThrow(() -> new HomeworkNotFoundException(id));
        homeworkRepository.delete(foundHomework);
    }

    public List<HomeworkDTO> findAllBySubject(Long subjectId) {
        var foundSubject = subjectsRepository.findById(subjectId)
                .orElseThrow(() -> new SubjectNotFoundException(subjectId));

        return foundSubject.getHomeworks().stream()
                .map(hw -> HomeworkDTO
                        .builder()
                        .id(hw.getId())
                        .title(hw.getTitle())
                        .description(hw.getDescription())
                        .deadline(hw.getDeadline())
                        .build())
                .toList();


    }

    public void update(Long id, HomeworkDTO homeworkDTO) {
        var foundHomework = homeworkRepository.findById(id)
                .orElseThrow(() -> new HomeworkNotFoundException(id));

        foundHomework.setTitle(homeworkDTO.title());
        foundHomework.setDescription(homeworkDTO.description());
        foundHomework.setDeadline(homeworkDTO.deadline());
        homeworkRepository.save(foundHomework);
    }
}
