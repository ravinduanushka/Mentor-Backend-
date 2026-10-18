package com.mentorconnect.backend.domains.admin;

import jakarta.validation.constraints.NotNull;

public record ResolveReportRequest(
        @NotNull Report.ReportStatus status,
        String resolutionNotes
) {}