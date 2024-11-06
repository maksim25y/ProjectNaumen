package ru.mudan.services.grades;

import java.util.List;
import java.util.NoSuchElementException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
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
                        .mark(grade.getMark())
                        .dateOfMark(grade.getDateOfMark())
                        .comment(grade.getComment())
                        .studentId(studentId)
                        .subjectId(subjectId)
                        .build())
                .toList();
    }

    public GradeDTO findById(Long id) {
        return null;
    }

    public void save(GradeDTO request) {

    }

    public void update(GradeDTO request, Long id) {

    }

    public void deleteById(Long id) {

    }
}
