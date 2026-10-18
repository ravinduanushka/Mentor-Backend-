package com.mentorconnect.backend.domains.user;

import java.util.UUID;

public record UserProfileResponse(
        UUID id,
        String externalAuthId,
        String email,
        String displayName,
        User.Role role,
        User.AccountStatus accountStatus
) {
    public static UserProfileResponse fromEntity(User user) {
        return new UserProfileResponse(
                user.getId(),
                user.getExternalAuthId(),
                user.getEmail(),
                user.getDisplayName(),
                user.getRole(),
                user.getAccountStatus()
        );
    }
}