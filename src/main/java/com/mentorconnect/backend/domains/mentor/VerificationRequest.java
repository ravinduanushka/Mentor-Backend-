package com.mentorconnect.backend.domains.mentor;

import jakarta.validation.constraints.NotBlank;

public record VerificationRequest(
        @NotBlank String documentUrl,
        String documentType
) {}