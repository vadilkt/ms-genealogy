package com.example.genealogie.entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;
import java.util.List;

@Getter
@Setter
@Entity
public class Profile {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "user_id")
    private User user;
    private String nom;
    private String prenom;
    private String telephone;
    private String ville;
    private String pays;
    private Date dateNaissance;
    private Date dateMort;

    @ElementCollection
    private List<String> contacts;

    @OneToMany(mappedBy = "profile", cascade = CascadeType.ALL)
    private List<ParcoursAcademique> parcoursAcademiques;

    @OneToMany(mappedBy = "profile", cascade = CascadeType.ALL)
    private List<ExperienceProfessionnelle> experienceProfessionnelles;

    @OneToMany(mappedBy = "profile", cascade = CascadeType.ALL)
    private List<RelationFamille> relationFamilles;
}
