package ru.mudan.services.grades;

import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.mudan.domain.entity.Grade;
import ru.mudan.domain.repositories.GradeRepository;
import ru.mudan.domain.repositories.StudentRepository;
import ru.mudan.domain.repositories.SubjectsRepository;
import ru.mudan.dto.grades.GradeDTO;
import ru.mudan.dto.grades.GradeDTOResponse;
import ru.mudan.exceptions.entity.not_found.GradeNotFoundException;
import ru.mudan.exceptions.entity.not_found.StudentNotFoundException;
import ru.mudan.exceptions.entity.not_found.SubjectNotFoundException;

@Slf4j
@Service
@RequiredArgsConstructor
public class GradesService {

    private final GradeRepository gradeRepository;
    private final StudentRepository studentRepository;
    private final SubjectsRepository subjectsRepository;

    public List<GradeDTO> findAllGradesForStudent(Long studentId) {
        log.info("Started getting all grades for student with id={}", studentId);
        var foundStudent = studentRepository.findById(studentId)
                .orElseThrow(() -> new StudentNotFoundException(studentId));


        var grades = foundStudent.getGrades();
        log.info("Finished getting all grades for student with id={}", studentId);

        return grades.stream()
                .map(grade -> GradeDTO
                        .builder()
                        .id(grade.getId())
                        .mark(grade.getMark())
                        .dateOfMark(grade.getDateOfMark())
                        .comment(grade.getComment())
                        .studentId(studentId)
                        .subjectId(grade.getSubject().getId())
                        .build())
                .toList();
    }

    public List<GradeDTO> findAllGradesForStudentWithSubject(Long studentId, Long subjectId) {
        log.info("Started getting all grades for student with id={} and subject with id={}", studentId, subjectId);
        var foundStudent = studentRepository.findById(studentId)
                .orElseThrow(() -> new StudentNotFoundException(studentId));

        var foundSubject = subjectsRepository.findById(subjectId)
                .orElseThrow(() -> new SubjectNotFoundException(subjectId));

        var grades = gradeRepository.findAllByStudentAndSubject(foundStudent, foundSubject);
        log.info("Finished getting all grades for student with id={} and subject with id={}", studentId, subjectId);

        return grades.stream()
                .map(grade -> GradeDTO
                        .builder()
                        .id(grade.getId())
                        .mark(grade.getMark())
                        .dateOfMark(grade.getDateOfMark())
                        .comment(grade.getComment())
                        .studentId(studentId)
                        .subjectId(subjectId)
                        .build())
                .toList();
    }

    public GradeDTO findById(Long id) {
        var foundGrade = gradeRepository.findById(id)
                .orElseThrow(() -> new GradeNotFoundException(id));

        return GradeDTO
                .builder()
                .id(foundGrade.getId())
                .mark(foundGrade.getMark())
                .dateOfMark(foundGrade.getDateOfMark())
                .comment(foundGrade.getComment())
                .build();
    }

    public void save(GradeDTO request) {
        log.info("Started creating grade for student with id={} and subject with id={}", request.studentId(), request.subjectId());
        var foundStudent = studentRepository.findById(request.studentId())
                .orElseThrow(() -> new StudentNotFoundException(request.studentId()));

        var foundSubject = subjectsRepository.findById(request.subjectId())
                .orElseThrow(() -> new SubjectNotFoundException(request.subjectId()));

        var grade = new Grade(
                request.mark(),
                request.dateOfMark(),
                request.comment());

        grade.setStudent(foundStudent);
        grade.setSubject(foundSubject);

        gradeRepository.save(grade);
        log.info("Finished creating grade for student with id={} and subject with id={}", request.studentId(), request.subjectId());
    }

    public List<GradeDTOResponse> findAllBySubjectId(Long subjectId) {
        log.info("Started getting all grades for subject with id={}", subjectId);
        var foundSubject = subjectsRepository.findById(subjectId)
                .orElseThrow(() -> new SubjectNotFoundException(subjectId));

        var grades = foundSubject.getGrades();
        log.info("Finished getting all grades for subject with id={}", subjectId);

        return grades.stream()
                .map(grade -> GradeDTOResponse
                        .builder()
                        .id(grade.getId())
                        .mark(grade.getMark())
                        .dateOfMark(grade.getDateOfMark())
                        .comment(grade.getComment())
                        .studentFirstname(grade.getStudent().getFirstname())
                        .studentLastname(grade.getStudent().getLastname())
                        .build())
                .toList();
    }

    public void update(GradeDTO request, Long id) {
        log.info("Started updating grade with id={}", id);
        var foundGrade = gradeRepository.findById(id)
                .orElseThrow(() -> new GradeNotFoundException(id));

        foundGrade.setMark(request.mark());
        foundGrade.setDateOfMark(request.dateOfMark());
        foundGrade.setComment(request.comment());
        gradeRepository.save(foundGrade);
        log.info("Finished updating grade with id={}", id);
    }

    public void deleteById(Long id) {
        log.info("Started deleting grade with id={}", id);
        var foundGrade = gradeRepository.findById(id)
                .orElseThrow(() -> new GradeNotFoundException(id));

        gradeRepository.delete(foundGrade);
        log.info("Finished deleting grade with id={}", id);
    }
}
