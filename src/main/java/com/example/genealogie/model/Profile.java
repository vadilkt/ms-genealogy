package com.example.genealogie.model;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.springframework.lang.Nullable;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "profile")
public class Profile {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private ZonedDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private ZonedDateTime updatedAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="user_id", nullable=true)
    private User user;
    private String firstName;
    private String lastName;
    private Gender gender;
    private ZonedDateTime dateOfBirth;
    @Nullable
    private ZonedDateTime dateOfDeath;

    /** Qualificatif/précision de la date de naissance (pour les dates incertaines). */
    @Enumerated(EnumType.STRING)
    @Column(name = "birth_date_qualifier")
    private DateQualifier birthDateQualifier = DateQualifier.EXACT;

    @Enumerated(EnumType.STRING)
    @Column(name = "birth_date_precision")
    private DatePrecision birthDatePrecision = DatePrecision.DAY;

    /** Qualificatif/précision de la date de décès. */
    @Enumerated(EnumType.STRING)
    @Column(name = "death_date_qualifier")
    private DateQualifier deathDateQualifier = DateQualifier.EXACT;

    @Enumerated(EnumType.STRING)
    @Column(name = "death_date_precision")
    private DatePrecision deathDatePrecision = DatePrecision.DAY;

    private String residence;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "birth_place_id")
    private Place birthPlace;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "death_place_id")
    private Place deathPlace;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "father_id")
    private Profile father;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "mother_id")
    private Profile mother;

    /** Mariages où ce profil est le mari (un homme peut avoir plusieurs épouses). */
    @OneToMany(mappedBy = "husband", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference("husband-marriages")
    private List<Marriage> marriagesAsHusband = new ArrayList<>();

    /**
     * Mariages où ce profil est l'épouse. Une femme n'a qu'un seul mari <em>actif</em>
     * (mariage sans date de fin) à la fois, mais peut se remarier après veuvage ou divorce.
     */
    @OneToMany(mappedBy = "wife", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference("wife-marriages")
    private List<Marriage> marriagesAsWife = new ArrayList<>();

    @OneToMany(mappedBy = "profile")
    @JsonManagedReference
    private List<ProfessionalProfile> professionalProfiles = new ArrayList<>();

    @OneToMany(mappedBy = "profile")
    @JsonManagedReference
    private List<AcademicProfile> academicProfiles = new ArrayList<>();
}


