package com.mentorconnect.backend.domains.mentor;

import com.mentorconnect.backend.domains.admin.MentorVerification;
import com.mentorconnect.backend.domains.user.User;
import com.mentorconnect.backend.domains.user.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/mentors")
@RequiredArgsConstructor
public class MentorController {

    private final MentorService mentorService;
    private final UserService userService;
    private final MentorProfileRepository mentorProfileRepository;

    // Public API: Approved Mentors ලැයිස්තුව බැලීම
    @GetMapping
    public ResponseEntity<List<MentorSummaryResponse>> getAllMentors() {
        return ResponseEntity.ok(mentorService.getAllApprovedMentors());
    }

    // Public API: එක් Mentor කෙනෙකුගේ විස්තර බැලීම
    @GetMapping("/{id}")
    public ResponseEntity<MentorProfile> getMentorById(@PathVariable UUID id) {
        return ResponseEntity.ok(mentorService.getMentorById(id));
    }

    // Public API: Mentor කෙනෙකුගේ Available Slots බැලීම
    @GetMapping("/{id}/slots")
    public ResponseEntity<List<AvailabilitySlot>> getMentorSlots(@PathVariable UUID id) {
        return ResponseEntity.ok(mentorService.getAvailableSlots(id));
    }

    // Mentor Only API: අලුත් Availability Slot එකක් සෑදීම
    @PostMapping("/me/slots")
    @PreAuthorize("hasRole('MENTOR')")
    public ResponseEntity<AvailabilitySlot> createSlot(
            @AuthenticationPrincipal Jwt jwt,
            @Valid @RequestBody CreateSlotRequest request
    ) {
        User user = userService.getUserByExternalAuthId(jwt.getClaimAsString("sub"));
        MentorProfile mentor = mentorProfileRepository.findByUserId(user.getId())
                .orElseThrow(() -> new RuntimeException("Mentor profile not found"));

        AvailabilitySlot slot = mentorService.createSlot(mentor.getId(), request);
        return ResponseEntity.ok(slot);
    }

    // Mentor Only API: Verification Document ඉදිරිපත් කිරීම
    @PostMapping("/me/verifications")
    @PreAuthorize("hasRole('MENTOR')")
    public ResponseEntity<MentorVerification> submitVerification(
            @AuthenticationPrincipal Jwt jwt,
            @Valid @RequestBody VerificationRequest request
    ) {
        User user = userService.getUserByExternalAuthId(jwt.getClaimAsString("sub"));
        MentorProfile mentor = mentorProfileRepository.findByUserId(user.getId())
                .orElseThrow(() -> new RuntimeException("Mentor profile not found"));

        MentorVerification verification = mentorService.submitVerification(mentor.getId(), request);
        return ResponseEntity.ok(verification);
    }
}