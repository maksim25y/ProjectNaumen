package ru.mudan.domain.repositories;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.mudan.domain.entity.Grade;
import ru.mudan.domain.entity.Subject;
import ru.mudan.domain.entity.users.Student;

@Repository
public interface GradeRepository extends JpaRepository<Grade, Long> {
    List<Grade> findAllByStudentAndSubject(Student student, Subject subject);
}
