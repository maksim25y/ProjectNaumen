package ru.mudan.services.homework;

import java.util.List;
import java.util.NoSuchElementException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.mudan.domain.entity.Homework;
import ru.mudan.domain.repositories.ClassRepository;
import ru.mudan.domain.repositories.HomeworkRepository;
import ru.mudan.domain.repositories.SubjectsRepository;
import ru.mudan.dto.HomeworkDTO;

@Service
@Transactional
@RequiredArgsConstructor
@SuppressWarnings("MemberName")
public class HomeworkService {

    private final String HOMEWORK_NOT_FOUND = "Homework not found";
    private final String CLASS_NOT_FOUND = "Class not found";
    private final String SUBJECT_NOT_FOUND = "Subject not found";

    private final HomeworkRepository homeworkRepository;
    private final ClassRepository classRepository;
    private final SubjectsRepository subjectsRepository;

    public List<HomeworkDTO> findAllByClassAndSubject(Long classId, Long subjectId) {
        var foundClass = classRepository.findById(classId)
                .orElseThrow(() -> new NoSuchElementException(CLASS_NOT_FOUND));
        var foundSubject = subjectsRepository.findById(subjectId)
                .orElseThrow(() -> new NoSuchElementException(SUBJECT_NOT_FOUND));

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

    public void save(HomeworkDTO hwDTO) {
        var foundClass = classRepository.findById(hwDTO.classId())
                .orElseThrow(() -> new NoSuchElementException(CLASS_NOT_FOUND));
        var foundSubject = subjectsRepository.findById(hwDTO.subjectId())
                .orElseThrow(() -> new NoSuchElementException(SUBJECT_NOT_FOUND));

        var homework = new Homework(hwDTO.title(), hwDTO.description(), hwDTO.deadline());
        homework.setClassEntity(foundClass);
        homework.setSubject(foundSubject);

        homeworkRepository.save(homework);
    }

    public HomeworkDTO findById(Long id) {
        var foundHomework = homeworkRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException(HOMEWORK_NOT_FOUND));

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
                .orElseThrow(() -> new NoSuchElementException(HOMEWORK_NOT_FOUND));
        homeworkRepository.delete(foundHomework);
    }

    public void update(Long id, HomeworkDTO homeworkDTO) {
        var foundHomework = homeworkRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException(HOMEWORK_NOT_FOUND));

        foundHomework.setTitle(homeworkDTO.title());
        foundHomework.setDescription(homeworkDTO.description());
        foundHomework.setDeadline(homeworkDTO.deadline());
        homeworkRepository.save(foundHomework);
    }
}
