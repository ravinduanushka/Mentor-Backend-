package com.mentorconnect.backend.domains.user;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping("/me")
    public ResponseEntity<UserProfileResponse> getCurrentUser(@AuthenticationPrincipal Jwt jwt) {
        User user = userService.syncUserFromJwt(jwt);
        return ResponseEntity.ok(UserProfileResponse.fromEntity(user));
    }

    @PutMapping("/me/student-profile")
    public ResponseEntity<StudentProfile> updateStudentProfile(
            @AuthenticationPrincipal Jwt jwt,
            @Valid @RequestBody UpdateStudentProfileRequest request
    ) {
        User user = userService.getUserByExternalAuthId(jwt.getClaimAsString("sub"));
        StudentProfile updated = userService.updateStudentProfile(user.getId(), request);
        return ResponseEntity.ok(updated);
    }
}