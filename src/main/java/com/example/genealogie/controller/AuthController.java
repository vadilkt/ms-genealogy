package com.example.genealogie.controller;

import com.example.genealogie.config.JwtUtil;
import com.example.genealogie.dto.AuthenticationRequest;
import com.example.genealogie.dto.AuthenticationResponse;
import com.example.genealogie.dto.UserRequestDto;
import com.example.genealogie.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final UserDetailsService userDetailsService;
    private final JwtUtil jwtUtil;
    private final UserService userService;

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody UserRequestDto userRequestDto) {
        try {
            log.info("Registering user: {}", userRequestDto.getUsername());
            userService.createUser(userRequestDto);
            return ResponseEntity.ok("Utilisateur créé");
        } catch (Exception e) {
            log.error("Registration failed: {}", e.getMessage());
            return ResponseEntity.badRequest().body("Erreur: " + e.getMessage());
        }
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody AuthenticationRequest request) {
        try {
            log.info("Login attempt for username: {}", request.getUsername());

            // Vérifier si l'utilisateur existe
            try {
                UserDetails userDetails = userDetailsService.loadUserByUsername(request.getUsername());
                log.info("User found in database: {}", userDetails.getUsername());
            } catch (Exception e) {
                log.error("User not found in database: {}", request.getUsername());
                return ResponseEntity.status(401).body("Utilisateur non trouvé");
            }

            // Tenter l'authentification
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword())
            );

            log.info("Authentication successful for: {}", request.getUsername());

            final UserDetails userDetails = userDetailsService.loadUserByUsername(request.getUsername());
            final String jwt = jwtUtil.generateToken(userDetails);

            return ResponseEntity.ok(new AuthenticationResponse(jwt));

        } catch (BadCredentialsException e) {
            log.error("Bad credentials for user: {}", request.getUsername());
            return ResponseEntity.status(401).body("Identifiants incorrects");
        } catch (Exception e) {
            log.error("Login failed for user: {}, error: {}", request.getUsername(), e.getMessage());
            return ResponseEntity.status(401).body("Erreur d'authentification");
        }
    }
}