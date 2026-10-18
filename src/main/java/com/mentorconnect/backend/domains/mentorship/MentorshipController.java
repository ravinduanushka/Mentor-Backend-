package com.mentorconnect.backend.domains.mentorship;

import com.mentorconnect.backend.domains.mentor.MentorProfile;
import com.mentorconnect.backend.domains.mentor.MentorProfileRepository;
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
@RequestMapping("/api/programs")
@RequiredArgsConstructor
public class MentorshipController {

    private final MentorshipService mentorshipService;
    private final UserService userService;
    private final MentorProfileRepository mentorProfileRepository;

    @GetMapping("/my-student-programs")
    @PreAuthorize("hasRole('STUDENT')")
    public ResponseEntity<List<MentorshipProgram>> getMyStudentPrograms(@AuthenticationPrincipal Jwt jwt) {
        User user = userService.getUserByExternalAuthId(jwt.getClaimAsString("sub"));
        return ResponseEntity.ok(mentorshipService.getStudentPrograms(user.getId()));
    }

    @GetMapping("/my-mentor-programs")
    @PreAuthorize("hasRole('MENTOR')")
    public ResponseEntity<List<MentorshipProgram>> getMyMentorPrograms(@AuthenticationPrincipal Jwt jwt) {
        User user = userService.getUserByExternalAuthId(jwt.getClaimAsString("sub"));
        MentorProfile mentor = mentorProfileRepository.findByUserId(user.getId())
                .orElseThrow(() -> new RuntimeException("Mentor profile not found"));
        return ResponseEntity.ok(mentorshipService.getMentorPrograms(mentor.getId()));
    }

    @GetMapping("/{programId}/goals")
    public ResponseEntity<List<Goal>> getProgramGoals(@PathVariable UUID programId) {
        return ResponseEntity.ok(mentorshipService.getProgramGoals(programId));
    }

    @PostMapping("/{programId}/goals")
    @PreAuthorize("hasRole('MENTOR')")
    public ResponseEntity<Goal> createGoal(
            @PathVariable UUID programId,
            @Valid @RequestBody CreateGoalRequest request
    ) {
        return ResponseEntity.ok(mentorshipService.createGoal(programId, request));
    }

    @PostMapping("/goals/{goalId}/tasks")
    public ResponseEntity<Task> createTask(
            @PathVariable UUID goalId,
            @Valid @RequestBody CreateTaskRequest request
    ) {
        return ResponseEntity.ok(mentorshipService.createTask(goalId, request));
    }

    @PatchMapping("/tasks/{taskId}/toggle")
    public ResponseEntity<Task> toggleTask(@PathVariable UUID taskId) {
        return ResponseEntity.ok(mentorshipService.toggleTaskCompletion(taskId));
    }

    @PostMapping("/{programId}/notes")
    @PreAuthorize("hasRole('MENTOR')")
    public ResponseEntity<MentorNote> addMentorNote(
            @AuthenticationPrincipal Jwt jwt,
            @PathVariable UUID programId,
            @Valid @RequestBody AddMentorNoteRequest request
    ) {
        User user = userService.getUserByExternalAuthId(jwt.getClaimAsString("sub"));
        MentorProfile mentor = mentorProfileRepository.findByUserId(user.getId())
                .orElseThrow(() -> new RuntimeException("Mentor profile not found"));
        return ResponseEntity.ok(mentorshipService.addMentorNote(programId, mentor, request));
    }

    @GetMapping("/{programId}/notes")
    @PreAuthorize("hasRole('MENTOR')")
    public ResponseEntity<List<MentorNote>> getMentorNotes(
            @AuthenticationPrincipal Jwt jwt,
            @PathVariable UUID programId
    ) {
        User user = userService.getUserByExternalAuthId(jwt.getClaimAsString("sub"));
        MentorProfile mentor = mentorProfileRepository.findByUserId(user.getId())
                .orElseThrow(() -> new RuntimeException("Mentor profile not found"));
        return ResponseEntity.ok(mentorshipService.getMentorNotes(programId, mentor.getId()));
    }
}