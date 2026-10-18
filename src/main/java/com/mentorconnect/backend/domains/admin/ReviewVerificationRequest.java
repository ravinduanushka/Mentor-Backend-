package com.mentorconnect.backend.domains.admin;

import jakarta.validation.constraints.NotNull;

public record ReviewVerificationRequest(
        @NotNull MentorVerification.VerificationStatus status,
        String rejectionReason
) {}