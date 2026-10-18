package com.mentorconnect.backend.domains.booking;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface BookingRepository extends JpaRepository<Booking, UUID> {
    List<Booking> findByStudentId(UUID studentId);
    List<Booking> findByMentorId(UUID mentorId);
    List<Booking> findByStatus(Booking.BookingStatus status);
}