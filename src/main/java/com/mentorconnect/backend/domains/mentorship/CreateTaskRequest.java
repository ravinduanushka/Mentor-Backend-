package com.mentorconnect.backend.domains.mentorship;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.time.LocalDate;

public record CreateTaskRequest(
        @NotBlank @Size(max = 255) String title,
        String description,
        LocalDate dueDate
) {}