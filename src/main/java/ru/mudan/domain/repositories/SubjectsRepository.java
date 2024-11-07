package ru.mudan.domain.repositories;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import ru.mudan.domain.entity.ClassEntity;
import ru.mudan.domain.entity.Subject;

public interface SubjectsRepository extends JpaRepository<Subject, Long> {
    Optional<Subject> findByCode(String code);

    List<Subject> findAllByClassEntity(ClassEntity classEntity);
}
