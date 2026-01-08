package com.example.genealogie.service;

import com.example.genealogie.dto.*;
import com.example.genealogie.model.Profile;
import com.example.genealogie.model.User;

import java.util.List;

/** Service pour les liens familiaux : parents, enfants, époux/épouses. */
public interface FamilyService {

    /** Définit le père d'un profil. */
    Profile setFather(Long profileId, Long fatherProfileId, User currentUser);

    /** Définit la mère d'un profil. */
    Profile setMother(Long profileId, Long motherProfileId, User currentUser);

    /** Supprime le lien vers le père. */
    void removeFather(Long profileId, User currentUser);

    /** Supprime le lien vers la mère. */
    void removeMother(Long profileId, User currentUser);

    /** Crée un mariage entre ce profil et le conjoint. Règle : un homme peut avoir plusieurs épouses, une femme un seul mari. */
    MarriageResponseDto addMarriage(Long profileId, CreateMarriageRequestDto request, User currentUser);

    /** Supprime un mariage. */
    void removeMarriage(Long marriageId, User currentUser);

    /** Retourne le père et la mère (peut être null). */
    FamilyResponseDto getParents(Long profileId, User currentUser);

    /** Retourne les enfants. */
    List<ProfileResponseDto> getChildren(Long profileId, User currentUser);

    /** Retourne les époux/épouses. */
    List<ProfileResponseDto> getSpouses(Long profileId, User currentUser);

    /** Retourne les frères et sœurs (profils ayant au moins un parent en commun). */
    List<ProfileResponseDto> getSiblings(Long profileId, User currentUser);

    /** Retourne l'arbre familial : parents, enfants, frères/sœurs, époux, mariages. */
    FamilyResponseDto getFamily(Long profileId, User currentUser);

    /**
     * Retourne tous les ancêtres (parents, grands-parents, arrière-grands-parents, etc.)
     * sous forme d'arbre récursif.
     * @param profileId id du profil
     * @param depth profondeur max (1 = parents uniquement, 2 = + grands-parents, etc.). Défaut 5.
     */
    AncestorNodeDto getAncestors(Long profileId, int depth, User currentUser);

    /**
     * Retourne tous les descendants (enfants, petits-enfants, etc.) sous forme d'arbre récursif.
     * @param profileId id du profil
     * @param depth profondeur max (1 = enfants uniquement, 2 = + petits-enfants, etc.). Défaut 5.
     */
    DescendantNodeDto getDescendants(Long profileId, int depth, User currentUser);
}
