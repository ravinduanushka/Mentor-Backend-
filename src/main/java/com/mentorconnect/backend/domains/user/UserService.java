package com.mentorconnect.backend.domains.user;

import lombok.RequiredArgsConstructor;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final StudentProfileRepository studentProfileRepository;

    @Transactional
    public User syncUserFromJwt(Jwt jwt) {
        String externalAuthId = jwt.getClaimAsString("sub");
        String email = jwt.getClaimAsString("email");
        String name = jwt.getClaimAsString("name");
        if (name == null || name.isBlank()) {
            name = email;
        }

        String roleClaim = jwt.getClaimAsString("role");
        User.Role role = User.Role.STUDENT;
        if (roleClaim != null) {
            try {
                role = User.Role.valueOf(roleClaim.toUpperCase());
            } catch (IllegalArgumentException ignored) {
            }
        }

        final User.Role finalRole = role;
        final String finalName = name;

        return userRepository.findByExternalAuthId(externalAuthId)
                .orElseGet(() -> {
                    User newUser = User.builder()
                            .externalAuthId(externalAuthId)
                            .email(email)
                            .displayName(finalName)
                            .role(finalRole)
                            .accountStatus(User.AccountStatus.ACTIVE)
                            .build();

                    User savedUser = userRepository.save(newUser);

                    if (savedUser.getRole() == User.Role.STUDENT) {
                        StudentProfile profile = StudentProfile.builder()
                                .user(savedUser)
                                .build();
                        studentProfileRepository.save(profile);
                    }

                    return savedUser;
                });
    }

    @Transactional(readOnly = true)
    public User getUserByExternalAuthId(String externalAuthId) {
        return userRepository.findByExternalAuthId(externalAuthId)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }

    @Transactional
    public StudentProfile updateStudentProfile(UUID userId, UpdateStudentProfileRequest request) {
        StudentProfile profile = studentProfileRepository.findByUserId(userId)
                .orElseThrow(() -> new RuntimeException("Student profile not found"));

        if (request.university() != null) profile.setUniversity(request.university());
        if (request.fieldOfStudy() != null) profile.setFieldOfStudy(request.fieldOfStudy());
        if (request.yearOfStudy() != null) profile.setYearOfStudy(request.yearOfStudy());
        if (request.bio() != null) profile.setBio(request.bio());
        if (request.linkedinUrl() != null) profile.setLinkedinUrl(request.linkedinUrl());

        return studentProfileRepository.save(profile);
    }
}