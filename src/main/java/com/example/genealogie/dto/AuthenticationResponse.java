package com.example.genealogie.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AuthenticationResponse {
    private final String jwt;
    private final Long id;
    private final String username;
    private final String email;
    private final String role;

    public AuthenticationResponse(String jwt, Long id, String username, String email, String role) {
        this.jwt = jwt;
        this.id = id;
        this.username = username;
        this.email = email;
        this.role = role;
    }
}
