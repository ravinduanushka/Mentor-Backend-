package com.mentorconnect.backend.domains.mentorship;

import jakarta.validation.constraints.NotBlank;

public record AddMentorNoteRequest(
        @NotBlank String noteContent
) {}