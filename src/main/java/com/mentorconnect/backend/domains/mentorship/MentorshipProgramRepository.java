package com.mentorconnect.backend.domains.mentorship;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface MentorshipProgramRepository extends JpaRepository<MentorshipProgram, UUID> {
    List<MentorshipProgram> findByStudentId(UUID studentId);
    List<MentorshipProgram> findByMentorId(UUID mentorId);
}