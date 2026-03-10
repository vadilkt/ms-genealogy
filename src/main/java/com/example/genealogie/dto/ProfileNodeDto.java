package com.example.genealogie.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.ZonedDateTime;

/**
 * Représentation légère d'un profil pour la construction de l'arbre global.
 * Contient uniquement les champs nécessaires au rendu et au calcul de position.
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ProfileNodeDto {
    private Long id;
    private String firstName;
    private String lastName;
    private String gender;
    private ZonedDateTime dateOfBirth;
    private ZonedDateTime dateOfDeath;
    private Long fatherId;
    private Long motherId;
}
