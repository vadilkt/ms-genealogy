package com.example.genealogie.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Avertissement de cohérence sur un profil (dates incohérentes, etc.).
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ValidationWarningDto {

    /** Code technique (ex. BIRTH_AFTER_DEATH, MARRIAGE_OUTSIDE_LIFE). */
    private String code;

    /** Message lisible pour l'utilisateur. */
    private String message;
}
