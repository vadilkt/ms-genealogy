package com.example.genealogie.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.annotation.Nullable;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldNameConstants;
import org.antlr.v4.runtime.misc.NotNull;

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
    @NotNull
    @JsonBackReference
    private Profile profile;

    @NotNull
    private String profession;

    @NotNull
    private String entreprise;

    @NotNull
    private String ville;

    @NotNull
    private ZonedDateTime dateDebut;

    @Nullable
    private ZonedDateTime dateFin;

    @Nullable
    private String description;
}
