package com.example.genealogie.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

/**
 * Nœud récursif pour l'arbre des descendants : chaque personne a des enfants,
 * qui ont eux-mêmes des enfants, etc.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DescendantNodeDto {

    /** Le profil de cette personne. */
    private ProfileResponseDto profile;

    /** Les enfants (liste vide si aucun ou si profondeur max atteinte). */
    private List<DescendantNodeDto> children;
}
