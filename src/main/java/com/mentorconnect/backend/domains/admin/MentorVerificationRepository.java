package com.mentorconnect.backend.domains.admin;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface MentorVerificationRepository extends JpaRepository<MentorVerification, UUID> {
    List<MentorVerification> findByMentorId(UUID mentorId);
    List<MentorVerification> findByStatus(MentorVerification.VerificationStatus status);
}