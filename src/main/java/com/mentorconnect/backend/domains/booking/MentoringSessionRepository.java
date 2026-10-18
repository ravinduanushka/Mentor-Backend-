package com.mentorconnect.backend.domains.booking;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface MentoringSessionRepository extends JpaRepository<MentoringSession, UUID> {
    Optional<MentoringSession> findByBookingId(UUID bookingId);
}