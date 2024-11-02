package ru.mudan.domain.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.mudan.domain.entity.users.Admin;

@Repository
public interface AdminRepository extends JpaRepository<Admin, Long> {
}
