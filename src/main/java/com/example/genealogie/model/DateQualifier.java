package com.example.genealogie.model;

/**
 * Qualificatif d'une date généalogique incertaine.
 * <ul>
 *   <li>{@code EXACT}  : date certaine.</li>
 *   <li>{@code ABOUT}  : date approximative (« vers »).</li>
 *   <li>{@code BEFORE} : antérieure à la date stockée (« avant »).</li>
 *   <li>{@code AFTER}  : postérieure à la date stockée (« après »).</li>
 * </ul>
 */
public enum DateQualifier {
    EXACT,
    ABOUT,
    BEFORE,
    AFTER
}
