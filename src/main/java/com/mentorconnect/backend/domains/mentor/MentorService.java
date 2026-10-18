package com.mentorconnect.backend.domains.mentor;

import com.mentorconnect.backend.domains.admin.MentorVerification;
import com.mentorconnect.backend.domains.admin.MentorVerificationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class MentorService {

    private final MentorProfileRepository mentorProfileRepository;
    private final AvailabilitySlotRepository availabilitySlotRepository;
    private final MentorVerificationRepository verificationRepository;

    @Transactional(readOnly = true)
    public List<MentorSummaryResponse> getAllApprovedMentors() {
        return mentorProfileRepository.findAll().stream()
                .filter(m -> m.getVerificationStatus() == MentorProfile.VerificationStatus.APPROVED)
                .map(MentorSummaryResponse::fromEntity)
                .toList();
    }

    @Transactional(readOnly = true)
    public MentorProfile getMentorById(UUID mentorId) {
        return mentorProfileRepository.findById(mentorId)
                .orElseThrow(() -> new RuntimeException("Mentor not found"));
    }

    @Transactional(readOnly = true)
    public List<AvailabilitySlot> getAvailableSlots(UUID mentorId) {
        return availabilitySlotRepository.findByMentorIdAndStatus(mentorId, AvailabilitySlot.SlotStatus.AVAILABLE);
    }

    @Transactional
    public AvailabilitySlot createSlot(UUID mentorId, CreateSlotRequest request) {
        MentorProfile mentor = getMentorById(mentorId);

        if (request.endTime().isBefore(request.startTime())) {
            throw new IllegalArgumentException("End time must be after start time");
        }

        AvailabilitySlot slot = AvailabilitySlot.builder()
                .mentor(mentor)
                .startTime(request.startTime())
                .endTime(request.endTime())
                .status(AvailabilitySlot.SlotStatus.AVAILABLE)
                .build();

        return availabilitySlotRepository.save(slot);
    }

    @Transactional
    public MentorVerification submitVerification(UUID mentorId, VerificationRequest request) {
        MentorProfile mentor = getMentorById(mentorId);

        MentorVerification verification = MentorVerification.builder()
                .mentor(mentor)
                .documentUrl(request.documentUrl())
                .documentType(request.documentType())
                .status(MentorVerification.VerificationStatus.PENDING)
                .build();

        return verificationRepository.save(verification);
    }
}
