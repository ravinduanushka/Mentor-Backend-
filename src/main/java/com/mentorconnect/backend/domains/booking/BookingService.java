package com.mentorconnect.backend.domains.booking;

import com.mentorconnect.backend.domains.mentor.AvailabilitySlot;
import com.mentorconnect.backend.domains.mentor.AvailabilitySlotRepository;
import com.mentorconnect.backend.domains.user.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class BookingService {

    private final BookingRepository bookingRepository;
    private final AvailabilitySlotRepository availabilitySlotRepository;

    @Transactional
    public BookingResponse createBooking(User student, CreateBookingRequest request) {

        AvailabilitySlot slot = availabilitySlotRepository.findAvailableSlotForUpdate(request.slotId())
                .orElseThrow(() -> new IllegalStateException("Slot is not available or already held/booked"));


        slot.setStatus(AvailabilitySlot.SlotStatus.HELD);
        availabilitySlotRepository.save(slot);

        Instant heldUntil = Instant.now().plus(15, ChronoUnit.MINUTES);

        Booking booking = Booking.builder()
                .student(student)
                .mentor(slot.getMentor())
                .slot(slot)
                .status(Booking.BookingStatus.PENDING)
                .heldUntil(heldUntil)
                .amount(slot.getMentor().getHourlyRate())
                .currency("USD")
                .build();

        Booking savedBooking = bookingRepository.save(booking);
        return BookingResponse.fromEntity(savedBooking);
    }

    @Transactional
    public BookingResponse rescheduleBooking(User student, UUID bookingId, RescheduleBookingRequest request) {
        Booking oldBooking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new RuntimeException("Booking not found"));

        if (!oldBooking.getStudent().getId().equals(student.getId())) {
            throw new SecurityException("Unauthorized to reschedule this booking");
        }

        if (oldBooking.getStatus() != Booking.BookingStatus.CONFIRMED) {
            throw new IllegalStateException("Only confirmed bookings can be rescheduled");
        }


        AvailabilitySlot newSlot = availabilitySlotRepository.findAvailableSlotForUpdate(request.newSlotId())
                .orElseThrow(() -> new IllegalStateException("New slot is not available"));


        AvailabilitySlot oldSlot = oldBooking.getSlot();
        oldSlot.setStatus(AvailabilitySlot.SlotStatus.AVAILABLE);
        availabilitySlotRepository.save(oldSlot);


        oldBooking.setStatus(Booking.BookingStatus.RESCHEDULED);
        bookingRepository.save(oldBooking);


        newSlot.setStatus(AvailabilitySlot.SlotStatus.BOOKED);
        availabilitySlotRepository.save(newSlot);


        Booking newBooking = Booking.builder()
                .student(student)
                .mentor(oldBooking.getMentor())
                .slot(newSlot)
                .status(Booking.BookingStatus.CONFIRMED)
                .amount(oldBooking.getAmount())
                .currency(oldBooking.getCurrency())
                .rescheduledFrom(oldBooking)
                .build();

        Booking savedNewBooking = bookingRepository.save(newBooking);
        return BookingResponse.fromEntity(savedNewBooking);
    }

    @Transactional(readOnly = true)
    public List<BookingResponse> getStudentBookings(UUID studentId) {
        return bookingRepository.findByStudentId(studentId).stream()
                .map(BookingResponse::fromEntity)
                .toList();
    }
}