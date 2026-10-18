package com.mentorconnect.backend.domains.mentor;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotNull;
import java.time.Instant;

public record CreateSlotRequest(
        @NotNull @Future Instant startTime,
        @NotNull @Future Instant endTime
) {}