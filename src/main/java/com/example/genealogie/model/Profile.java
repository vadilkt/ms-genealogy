package com.example.genealogie.model;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import jakarta.validation.constraints.NotNull;
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

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="user_id", nullable=true)
    private User user;
    @NotNull
    private String firstName;
    private String lastName;
    @NotNull
    private Gender gender;
    @NotNull
    private ZonedDateTime dateOfBirth;
    @Nullable
    private ZonedDateTime dateOfDeath;
    @NotNull
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

    /** Mariage où ce profil est l'épouse (une femme n'a qu'un seul mari). */
    @OneToOne(mappedBy = "wife", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference("wife-marriage")
    private Marriage marriageAsWife;

    @OneToMany(mappedBy = "profile")
    @JsonManagedReference
    private List<ProfessionalProfile> professionalProfiles = new ArrayList<>();

    @OneToMany(mappedBy = "profile")
    @JsonManagedReference
    private List<AcademicProfile> academicProfiles = new ArrayList<>();
}


