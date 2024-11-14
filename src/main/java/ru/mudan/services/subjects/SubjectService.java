package ru.mudan.services.subjects;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import ru.mudan.domain.entity.Subject;
import ru.mudan.domain.repositories.ClassRepository;
import ru.mudan.domain.repositories.SubjectsRepository;
import ru.mudan.domain.repositories.TeacherRepository;
import ru.mudan.dto.subjects.SubjectCreateDTO;
import ru.mudan.dto.subjects.SubjectDTO;
import ru.mudan.dto.subjects.SubjectUpdateDTO;
import ru.mudan.exceptions.entity.already_exists.SubjectAlreadyExistsException;
import ru.mudan.exceptions.entity.not_found.ClassEntityNotFoundException;
import ru.mudan.exceptions.entity.not_found.SubjectNotFoundException;
import ru.mudan.exceptions.entity.not_found.TeacherNotFoundException;

@Service
@RequiredArgsConstructor
public class SubjectService {

    private final TeacherRepository teacherRepository;
    @Value("${size.of.code}")
    private Integer sizeOfPartFromSubjectNameForSubjectCode;
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
                .orElseThrow(() -> new SubjectNotFoundException(id));

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

        var teacherForSubject = teacherRepository.findById(request.teacherId())
                .orElseThrow(() -> new TeacherNotFoundException(request.teacherId()));

        var codeForSb = generateCode(request.name(), classForSubject.getNumber(), classForSubject.getLetter());

        checkSubjectAlreadyExistsByCode(codeForSb);

        var subjectForSaving = new Subject(
                request.name(),
                request.type(),
                codeForSb,
                request.description());

        subjectForSaving.setClassEntity(classForSubject);
        subjectForSaving.setTeacher(teacherForSubject);

        subjectsRepository.save(subjectForSaving);
    }

    private String generateCode(String name, Integer classNumber, String letter) {
        return name.substring(0, sizeOfPartFromSubjectNameForSubjectCode).toUpperCase() + classNumber + letter;
    }

    public void update(SubjectUpdateDTO request, Long id) {
        var foundSubject = subjectsRepository.findById(id)
                .orElseThrow(() -> new SubjectNotFoundException(id));

        foundSubject.setType(request.type());
        foundSubject.setDescription(request.description());

        subjectsRepository.save(foundSubject);
    }

    public void deleteById(Long id) {
        var foundSubject = subjectsRepository
                .findById(id).orElseThrow(() -> new SubjectNotFoundException(id));
        subjectsRepository.delete(foundSubject);
    }

    public List<SubjectDTO> findAllSubjectsForClass(Long id) {
        var foundClass = classRepository.findById(id)
                .orElseThrow(() -> new ClassEntityNotFoundException(id));

        return foundClass.getSubjects()
                .stream()
                .map(sb -> SubjectDTO
                        .builder()
                        .id(sb.getId())
                        .code(sb.getCode())
                        .type(sb.getType())
                        .name(sb.getName())
                        .description(sb.getDescription())
                        .build())
                .toList();
    }

    public List<SubjectDTO> getSubjectsForTeacher(Long teacherId) {
        var foundTeacher = teacherRepository.findById(teacherId)
                .orElseThrow(() -> new TeacherNotFoundException(teacherId));

        return foundTeacher.getSubjects().stream()
                .map(sb -> SubjectDTO
                        .builder()
                        .id(sb.getId())
                        .code(sb.getCode())
                        .type(sb.getType())
                        .description(sb.getDescription())
                        .name(sb.getName())
                        .build())
                .toList();
    }

    private void checkSubjectAlreadyExistsByCode(String code) {
        var foundSubject = subjectsRepository.findByCode(code).orElse(null);

        if (foundSubject != null) {
            var classForSubject = foundSubject.getClassEntity();
            throw new SubjectAlreadyExistsException(
                    foundSubject.getName(),
                    classForSubject.getNumber(),
                    classForSubject.getLetter());
        }
    }
}
