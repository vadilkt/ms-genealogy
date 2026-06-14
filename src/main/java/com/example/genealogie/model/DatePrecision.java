package com.example.genealogie.model;

/**
 * Précision connue d'une date généalogique : seul l'élément le plus significatif
 * peut être fiable (année seule, mois + année, ou jour complet).
 */
public enum DatePrecision {
    DAY,
    MONTH,
    YEAR
}
