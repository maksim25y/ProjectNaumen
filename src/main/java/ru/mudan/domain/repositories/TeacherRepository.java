package ru.mudan.domain.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.mudan.domain.entity.users.Teacher;

/**
 * Репозиторий для работы с сущностью Teacher
 */
@Repository
public interface TeacherRepository extends JpaRepository<Teacher, Long> {
}
