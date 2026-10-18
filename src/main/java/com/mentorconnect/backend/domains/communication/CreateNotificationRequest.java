package com.mentorconnect.backend.domains.communication;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.util.UUID;

public record CreateNotificationRequest(
        @NotNull UUID userId,
        @NotBlank String type,
        @NotBlank String title,
        @NotBlank String message
) {}