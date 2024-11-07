package ru.mudan.services.grades;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.mudan.domain.entity.Grade;
import ru.mudan.domain.repositories.GradeRepository;
import ru.mudan.domain.repositories.StudentRepository;
import ru.mudan.domain.repositories.SubjectsRepository;
import ru.mudan.dto.grades.GradeDTO;
import ru.mudan.exceptions.entity.not_found.GradeNotFoundException;
import ru.mudan.exceptions.entity.not_found.StudentNotFoundException;
import ru.mudan.exceptions.entity.not_found.SubjectNotFoundException;

@Service
@RequiredArgsConstructor
public class GradesService {

    private final GradeRepository gradeRepository;
    private final StudentRepository studentRepository;
    private final SubjectsRepository subjectsRepository;

    public List<GradeDTO> findAllGradesForStudent(Long studentId) {
        var foundStudent = studentRepository.findById(studentId)
                .orElseThrow(() -> new StudentNotFoundException(studentId));


        var grades = foundStudent.getGrades();

        return grades
                .stream()
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
        var foundStudent = studentRepository.findById(studentId)
                .orElseThrow(() -> new StudentNotFoundException(studentId));

        var foundSubject = subjectsRepository.findById(subjectId)
                .orElseThrow(() -> new SubjectNotFoundException(subjectId));

        var grades = gradeRepository.findAllByStudentAndSubject(foundStudent, foundSubject);

        return grades
                .stream()
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
    }

    public void update(GradeDTO request, Long id) {
        var foundGrade = gradeRepository.findById(id)
                .orElseThrow(() -> new GradeNotFoundException(id));

        foundGrade.setMark(request.mark());
        foundGrade.setDateOfMark(request.dateOfMark());
        foundGrade.setComment(request.comment());
        gradeRepository.save(foundGrade);
    }

    public void deleteById(Long id) {
        var foundGrade = gradeRepository.findById(id)
                .orElseThrow(() -> new GradeNotFoundException(id));

        gradeRepository.delete(foundGrade);
    }
}
