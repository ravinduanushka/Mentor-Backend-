package com.mentorconnect.backend.domains.booking;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public record BookingResponse(
        UUID id,
        UUID studentId,
        UUID mentorId,
        UUID slotId,
        Booking.BookingStatus status,
        Instant heldUntil,
        BigDecimal amount,
        String currency
) {
    public static BookingResponse fromEntity(Booking booking) {
        return new BookingResponse(
                booking.getId(),
                booking.getStudent().getId(),
                booking.getMentor().getId(),
                booking.getSlot().getId(),
                booking.getStatus(),
                booking.getHeldUntil(),
                booking.getAmount(),
                booking.getCurrency()
        );
    }
}