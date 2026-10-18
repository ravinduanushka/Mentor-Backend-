package com.mentorconnect.backend.domains.mentor;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface ExpertiseRepository extends JpaRepository<Expertise, UUID> {
}