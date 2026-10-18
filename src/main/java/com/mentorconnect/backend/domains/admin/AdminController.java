package com.mentorconnect.backend.domains.admin;

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
@RequestMapping("/api/admin")
@PreAuthorize("hasRole('ADMIN')")
@RequiredArgsConstructor
public class AdminController {

    private final AdminService adminService;
    private final UserService userService;

    @GetMapping("/verifications/pending")
    public ResponseEntity<List<MentorVerification>> getPendingVerifications() {
        return ResponseEntity.ok(adminService.getPendingVerifications());
    }

    @PatchMapping("/verifications/{id}/review")
    public ResponseEntity<MentorVerification> reviewVerification(
            @AuthenticationPrincipal Jwt jwt,
            @PathVariable UUID id,
            @Valid @RequestBody ReviewVerificationRequest request
    ) {
        User adminUser = userService.getUserByExternalAuthId(jwt.getClaimAsString("sub"));
        return ResponseEntity.ok(adminService.reviewVerification(id, adminUser, request));
    }

    @GetMapping("/reports/open")
    public ResponseEntity<List<Report>> getOpenReports() {
        return ResponseEntity.ok(adminService.getOpenReports());
    }

    @PatchMapping("/reports/{id}/resolve")
    public ResponseEntity<Report> resolveReport(
            @AuthenticationPrincipal Jwt jwt,
            @PathVariable UUID id,
            @Valid @RequestBody ResolveReportRequest request
    ) {
        User adminUser = userService.getUserByExternalAuthId(jwt.getClaimAsString("sub"));
        return ResponseEntity.ok(adminService.resolveReport(id, adminUser, request));
    }

    @GetMapping("/audit-logs/{entity}/{targetId}")
    public ResponseEntity<List<AuditLog>> getAuditLogs(
            @PathVariable String entity,
            @PathVariable UUID targetId
    ) {
        return ResponseEntity.ok(adminService.getAuditLogsForEntity(entity, targetId));
    }
}