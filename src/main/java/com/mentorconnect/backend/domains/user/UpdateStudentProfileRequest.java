package com.mentorconnect.backend.domains.user;

import jakarta.validation.constraints.Size;

public record UpdateStudentProfileRequest(
        @Size(max = 255) String university,
        @Size(max = 255) String fieldOfStudy,
        Integer yearOfStudy,
        String bio,
        @Size(max = 512) String linkedinUrl
) {}