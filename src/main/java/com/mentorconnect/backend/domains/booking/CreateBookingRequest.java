package com.mentorconnect.backend.domains.booking;

import jakarta.validation.constraints.NotNull;
import java.util.UUID;

public record CreateBookingRequest(
        @NotNull UUID slotId
) {}