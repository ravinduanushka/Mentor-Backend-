package com.mentorconnect.backend.domains.user;

import com.mentorconnect.backend.common.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "student_profiles")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StudentProfile extends BaseEntity {

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", referencedColumnName = "id", nullable = false, unique = true)
    private User user;

    @Column(length = 255)
    private String university;

    @Column(name = "field_of_study", length = 255)
    private String fieldOfStudy;

    @Column(name = "year_of_study")
    private Integer yearOfStudy;

    @Column(columnDefinition = "TEXT")
    private String bio;

    @Column(name = "linkedin_url", length = 512)
    private String linkedinUrl;
}
