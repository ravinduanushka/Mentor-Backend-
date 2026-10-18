package com.mentorconnect.backend.domains.admin;

import com.mentorconnect.backend.domains.mentor.MentorProfile;
import com.mentorconnect.backend.domains.mentor.MentorProfileRepository;
import com.mentorconnect.backend.domains.user.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AdminService {

    private final MentorVerificationRepository verificationRepository;
    private final MentorProfileRepository mentorProfileRepository;
    private final ReportRepository reportRepository;
    private final AuditLogRepository auditLogRepository;

    @Transactional(readOnly = true)
    public List<MentorVerification> getPendingVerifications() {
        return verificationRepository.findByStatus(MentorVerification.VerificationStatus.PENDING);
    }

    @Transactional
    public MentorVerification reviewVerification(UUID verificationId, User adminUser, ReviewVerificationRequest request) {
        MentorVerification verification = verificationRepository.findById(verificationId)
                .orElseThrow(() -> new RuntimeException("Verification request not found"));

        verification.setStatus(request.status());
        verification.setReviewedBy(adminUser);
        verification.setReviewedAt(Instant.now());

        if (request.status() == MentorVerification.VerificationStatus.REJECTED) {
            verification.setRejectionReason(request.rejectionReason());
        }

        MentorProfile mentor = verification.getMentor();
        if (request.status() == MentorVerification.VerificationStatus.APPROVED) {
            mentor.setVerificationStatus(MentorProfile.VerificationStatus.APPROVED);
        } else if (request.status() == MentorVerification.VerificationStatus.REJECTED) {
            mentor.setVerificationStatus(MentorProfile.VerificationStatus.REJECTED);
        }
        mentorProfileRepository.save(mentor);

        recordAuditLog(adminUser, "VERIFY_MENTOR", "MentorProfile", mentor.getId(), "Status: " + request.status());

        return verificationRepository.save(verification);
    }

    @Transactional(readOnly = true)
    public List<Report> getOpenReports() {
        return reportRepository.findByStatus(Report.ReportStatus.OPEN);
    }

    @Transactional
    public Report resolveReport(UUID reportId, User adminUser, ResolveReportRequest request) {
        Report report = reportRepository.findById(reportId)
                .orElseThrow(() -> new RuntimeException("Report not found"));

        report.setStatus(request.status());
        report.setResolvedBy(adminUser);
        report.setResolutionNotes(request.resolutionNotes());
        report.setResolvedAt(Instant.now());

        recordAuditLog(adminUser, "RESOLVE_REPORT", "Report", report.getId(), "Status: " + request.status());

        return reportRepository.save(report);
    }

    @Transactional
    public void recordAuditLog(User actor, String action, String targetEntity, UUID targetId, String metadata) {
        AuditLog log = AuditLog.builder()
                .actor(actor)
                .action(action)
                .targetEntity(targetEntity)
                .targetId(targetId)
                .metadata(metadata)
                .build();
        auditLogRepository.save(log);
    }

    @Transactional(readOnly = true)
    public List<AuditLog> getAuditLogsForEntity(String entity, UUID targetId) {
        return auditLogRepository.findByTargetEntityAndTargetId(entity, targetId);
    }
}