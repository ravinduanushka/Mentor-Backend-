package com.mentorconnect.backend.domains.user;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserRepository extends JpaRepository<User, UUID> {
    Optional<User> findByExternalAuthId(String externalAuthId);
    Optional<User> findByEmail(String email);
    boolean existsByEmail(String email);
}
