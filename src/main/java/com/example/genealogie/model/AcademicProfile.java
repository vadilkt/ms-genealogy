package com.example.genealogie.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.annotation.Nullable;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldNameConstants;

import java.time.ZonedDateTime;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@FieldNameConstants
@Table(name = "academic_profile")
public class AcademicProfile {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "profile_id")
    @NonNull
    @JsonBackReference
    private Profile profile;

    @NonNull
    private String institution;

    @NonNull
    private String degree;

    @NonNull
    private String fieldOfStudy;

    @NonNull
    private ZonedDateTime startDate;

    @Nullable
    private ZonedDateTime endDate;

    @Nullable
    private String grade;


}
