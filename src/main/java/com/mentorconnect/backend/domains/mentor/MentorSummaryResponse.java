package com.mentorconnect.backend.domains.mentor;

import java.math.BigDecimal;
import java.util.UUID;

public record MentorSummaryResponse(
        UUID id,
        String displayName,
        String jobTitle,
        String company,
        BigDecimal hourlyRate,
        BigDecimal ratingAverage,
        Integer totalReviews
) {
    public static MentorSummaryResponse fromEntity(MentorProfile profile) {
        return new MentorSummaryResponse(
                profile.getId(),
                profile.getUser().getDisplayName(),
                profile.getJobTitle(),
                profile.getCompany(),
                profile.getHourlyRate(),
                profile.getRatingAverage(),
                profile.getTotalReviews()
        );
    }
}