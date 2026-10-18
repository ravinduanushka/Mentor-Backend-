package com.mentorconnect.backend.domains.booking;

import com.mentorconnect.backend.domains.user.User;
import com.mentorconnect.backend.domains.user.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/bookings")
@RequiredArgsConstructor
public class BookingController {

    private final BookingService bookingService;
    private final UserService userService;


    @PostMapping
    @PreAuthorize("hasRole('STUDENT')")
    public ResponseEntity<BookingResponse> createBooking(
            @AuthenticationPrincipal Jwt jwt,
            @Valid @RequestBody CreateBookingRequest request
    ) {
        User student = userService.getUserByExternalAuthId(jwt.getClaimAsString("sub"));
        BookingResponse response = bookingService.createBooking(student, request);
        return ResponseEntity.ok(response);
    }


    @PostMapping("/{id}/reschedule")
    @PreAuthorize("hasRole('STUDENT')")
    public ResponseEntity<BookingResponse> rescheduleBooking(
            @AuthenticationPrincipal Jwt jwt,
            @PathVariable UUID id,
            @Valid @RequestBody RescheduleBookingRequest request
    ) {
        User student = userService.getUserByExternalAuthId(jwt.getClaimAsString("sub"));
        BookingResponse response = bookingService.rescheduleBooking(student, id, request);
        return ResponseEntity.ok(response);
    }


    @GetMapping("/my")
    @PreAuthorize("hasRole('STUDENT')")
    public ResponseEntity<List<BookingResponse>> getMyBookings(@AuthenticationPrincipal Jwt jwt) {
        User student = userService.getUserByExternalAuthId(jwt.getClaimAsString("sub"));
        return ResponseEntity.ok(bookingService.getStudentBookings(student.getId()));
    }
}