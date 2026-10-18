package com.mentorconnect.backend.domains.mentorship;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.time.LocalDate;

public record CreateGoalRequest(
        @NotBlank @Size(max = 200) String title,
        String description,
        LocalDate targetDate
) {}