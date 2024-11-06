package ru.mudan.services.grades;

import java.util.List;
import java.util.NoSuchElementException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.mudan.domain.entity.Grade;
import ru.mudan.domain.repositories.GradeRepository;
import ru.mudan.domain.repositories.StudentRepository;
import ru.mudan.domain.repositories.SubjectsRepository;
import ru.mudan.dto.grades.GradeDTO;

@Service
@RequiredArgsConstructor
@SuppressWarnings("MultipleStringLiterals")
public class GradesService {

    private final GradeRepository gradeRepository;
    private final StudentRepository studentRepository;
    private final SubjectsRepository subjectsRepository;


    public List<GradeDTO> findAllGradesForStudent(Long studentId) {
        var foundStudent = studentRepository.findById(studentId)
                .orElseThrow(() -> new NoSuchElementException("Student not found"));


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
                .orElseThrow(() -> new NoSuchElementException("Student not found"));

        var foundSubject = subjectsRepository.findById(subjectId)
                .orElseThrow(() -> new NoSuchElementException("Subject not found"));

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
                .orElseThrow(() -> new NoSuchElementException("Grade not found"));

        return GradeDTO
                .builder()
                .mark(foundGrade.getMark())
                .dateOfMark(foundGrade.getDateOfMark())
                .comment(foundGrade.getComment())
                .build();
    }

    public void save(GradeDTO request) {
        var foundStudent = studentRepository.findById(request.studentId())
                .orElseThrow(() -> new NoSuchElementException("Student not found"));

        var foundSubject = subjectsRepository.findById(request.subjectId())
                .orElseThrow(() -> new NoSuchElementException("Subject not found"));

        var grade = new Grade(
                request.mark(),
                request.dateOfMark(),
                request.comment());

        grade.setStudent(foundStudent);
        grade.setSubject(foundSubject);

        gradeRepository.save(grade);
    }

    public void update(GradeDTO request, Long id) {

    }

    public void deleteById(Long id) {

    }
}
