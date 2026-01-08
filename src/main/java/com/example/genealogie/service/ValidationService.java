package com.example.genealogie.service;

import com.example.genealogie.dto.ValidationWarningDto;
import com.example.genealogie.model.User;

import java.util.List;

/**
 * Service de validation des données (cohérence des dates, etc.).
 */
public interface ValidationService {

    /**
     * Retourne la liste des avertissements de cohérence pour un profil.
     * Ex. : date de naissance après date de décès, mariage hors des dates de vie, etc.
     */
    List<ValidationWarningDto> getProfileWarnings(Long profileId, User currentUser);
}
