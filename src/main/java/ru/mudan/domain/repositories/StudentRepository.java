package ru.mudan.domain.repositories;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.mudan.domain.entity.ClassEntity;
import ru.mudan.domain.entity.users.Parent;
import ru.mudan.domain.entity.users.Student;

/**
 * Репозиторий для работы с сущностью Student
 */
@Repository
public interface StudentRepository extends JpaRepository<Student, Long> {
    List<Student> findAllByClassEntity(ClassEntity classEntity);

    List<Student> findAllByParent(Parent parent);
}
