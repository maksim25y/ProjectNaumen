package ru.mudan.domain.repositories;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.mudan.domain.entity.ClassEntity;

@Repository
public interface ClassRepository extends JpaRepository<ClassEntity, Long> {
    Optional<ClassEntity> findByLetterAndNumber(String letter, Integer number);
}
