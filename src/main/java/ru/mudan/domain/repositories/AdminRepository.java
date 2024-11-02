package ru.mudan.domain.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.mudan.domain.entity.users.Admin;

public interface AdminRepository extends JpaRepository<Admin, Integer> {
}
