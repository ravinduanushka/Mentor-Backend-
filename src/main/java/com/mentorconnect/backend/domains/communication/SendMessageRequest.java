package com.mentorconnect.backend.domains.communication;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.util.UUID;

public record SendMessageRequest(
        @NotNull UUID receiverId,
        @NotBlank String content
) {}