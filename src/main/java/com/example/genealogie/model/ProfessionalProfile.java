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
@Table(name = "professional_profile")
public class ProfessionalProfile {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "profile_id")
    @NonNull
    @JsonBackReference
    private Profile profile;

    @NonNull
    private String profession;

    @NonNull
    private String entreprise;

    @NonNull
    private String ville;

    @NonNull
    private ZonedDateTime dateDebut;

    @Nullable
    private ZonedDateTime dateFin;

    @Nullable
    private String description;
}
