package com.example.genealogie.util;

public class ProfileNotFoundException extends RuntimeException {

    public ProfileNotFoundException() {
        super("Profil non trouvé");
    }


    public ProfileNotFoundException(String message) {
        super(message);
    }

    // Constructeur avec message personnalisé et cause
    public ProfileNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }

    // Constructeur avec cause
    public ProfileNotFoundException(Throwable cause) {
        super(cause);
    }
}
