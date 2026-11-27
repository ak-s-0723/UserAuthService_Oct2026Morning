package org.example.userauthservice_oct2026morning.repos;

import org.example.userauthservice_oct2026morning.models.UserSession;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserSessionRepo extends JpaRepository<UserSession,Long> {
    Optional<UserSession>  findByToken(String value);
}
