package ru.mudan.domain.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.mudan.domain.entity.Schedule;

/**
 * Репозиторий для работы с сущностью Schedule
 */
@Repository
public interface ScheduleRepository extends JpaRepository<Schedule, Long> {
}
