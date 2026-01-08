package com.example.genealogie.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

/** Réponse pour l'arbre familial : parents, enfants, frères/sœurs, mariages (époux/épouses). */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FamilyResponseDto {

    private ProfileResponseDto father;
    private ProfileResponseDto mother;
    private List<ProfileResponseDto> children;
    /** Frères et sœurs (au moins un parent en commun). */
    private List<ProfileResponseDto> siblings;
    /** Pour un homme : ses épouses ; pour une femme : son mari (liste d'un élément). */
    private List<ProfileResponseDto> spouses;
    /** Détail des mariages (avec dates) pour ce profil. */
    private List<MarriageResponseDto> marriages;
}
