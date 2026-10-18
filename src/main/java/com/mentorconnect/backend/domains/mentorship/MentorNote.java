package com.mentorconnect.backend.domains.mentorship;

import com.mentorconnect.backend.common.BaseEntity;
import com.mentorconnect.backend.domains.mentor.MentorProfile;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "mentor_notes")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MentorNote extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "program_id", nullable = false)
    private MentorshipProgram program;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "mentor_id", nullable = false)
    private MentorProfile mentor;

    @Column(name = "note_content", columnDefinition = "TEXT", nullable = false)
    private String noteContent;
}