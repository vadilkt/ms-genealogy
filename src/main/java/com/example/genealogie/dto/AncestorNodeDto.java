package com.example.genealogie.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Noeud récursif pour l'arbre des ancêtres : chaque personne a un père et une mère,
 * qui ont eux-mêmes leur père et mère, etc.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AncestorNodeDto {

    /** Le profil de cette personne. */
    private ProfileResponseDto profile;

    /** Le père (null si inconnu ou si profondeur max atteinte). */
    private AncestorNodeDto father;

    /** La mère (null si inconnue ou si profondeur max atteinte). */
    private AncestorNodeDto mother;
}
