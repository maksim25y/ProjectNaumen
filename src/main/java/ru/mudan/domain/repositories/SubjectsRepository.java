package ru.mudan.domain.repositories;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import ru.mudan.domain.entity.Subject;

public interface SubjectsRepository extends JpaRepository<Subject, Long> {
    Optional<Subject> findByCode(String code);
}
