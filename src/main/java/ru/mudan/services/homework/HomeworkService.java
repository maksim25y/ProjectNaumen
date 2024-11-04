package ru.mudan.services.homework;

import java.util.List;
import java.util.NoSuchElementException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.mudan.domain.repositories.ClassRepository;
import ru.mudan.domain.repositories.HomeworkRepository;
import ru.mudan.domain.repositories.SubjectsRepository;
import ru.mudan.dto.HomeworkDTO;

@Service
@Transactional
@RequiredArgsConstructor
public class HomeworkService {

    private final HomeworkRepository homeworkRepository;
    private final ClassRepository classRepository;
    private final SubjectsRepository subjectsRepository;

    public List<HomeworkDTO> findAllByClassAndSubject(Long classId, Long subjectId) {
        var foundClass = classRepository.findById(classId)
                .orElseThrow(() -> new NoSuchElementException("Class not found"));
        var foundSubject = subjectsRepository.findById(subjectId)
                .orElseThrow(() -> new NoSuchElementException("Subject not found"));

        var listOfHomework = homeworkRepository.findByClassEntityAndSubject(foundClass, foundSubject);
        return listOfHomework
                .stream()
                .map(hw -> HomeworkDTO
                        .builder()
                        .title(hw.getTitle())
                        .description(hw.getDescription())
                        .deadline(hw.getDeadline())
                        .build())
                .toList();
    }
}
