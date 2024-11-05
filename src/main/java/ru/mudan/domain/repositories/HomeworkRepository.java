package ru.mudan.domain.repositories;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import ru.mudan.domain.entity.ClassEntity;
import ru.mudan.domain.entity.Homework;
import ru.mudan.domain.entity.Subject;

public interface HomeworkRepository extends JpaRepository<Homework, Long> {
    List<Homework> findByClassEntityAndSubject(ClassEntity classEntity, Subject subject);
}
