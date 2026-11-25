package org.example.userauthservice_oct2026morning.repos;

import org.example.userauthservice_oct2026morning.models.Role;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RoleRepo extends JpaRepository<Role,Long> {

    Optional<Role> findByValue(String value);
}
