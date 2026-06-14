package com.example.genealogie.service;

import com.example.genealogie.model.User;

public interface GedcomService {

    /** Exporte tous les profils et liens familiaux au format GEDCOM 5.5.1. */
    String exportGedcom();

    /**
     * Importe un fichier GEDCOM : crée les profils (INDI) et les liens familiaux
     * (parenté via les enregistrements FAM, mariages via les balises MARR).
     *
     * @return nombre de profils créés.
     */
    int importGedcom(String gedcom, User currentUser);
}
