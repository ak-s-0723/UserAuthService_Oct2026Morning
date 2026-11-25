package org.example.userauthservice_oct2026morning.repos;

import org.example.userauthservice_oct2026morning.models.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepo extends JpaRepository<User,Long> {
    Optional<User> findByEmail(String email);
}
