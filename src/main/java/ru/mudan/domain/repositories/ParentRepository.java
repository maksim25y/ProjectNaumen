package ru.mudan.domain.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.mudan.domain.entity.users.Parent;

/**
 * Репозиторий для работы с сущностью Parent
 */
@Repository
public interface ParentRepository extends JpaRepository<Parent, Long> {
}
