package com.example.genealogie.controller;

import com.example.genealogie.config.JwtUtil;
import com.example.genealogie.dto.AuthenticationRequest;
import com.example.genealogie.dto.AuthenticationResponse;
import com.example.genealogie.dto.UserRequestDto;
import com.example.genealogie.model.User;
import com.example.genealogie.service.ProfileService;
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
import jakarta.validation.Valid;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final UserDetailsService userDetailsService;
    private final JwtUtil jwtUtil;
    private final UserService userService;
    private final ProfileService profileService;

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody @Valid UserRequestDto userRequestDto) {
        log.info("Inscription de l'utilisateur : {}", userRequestDto.getUsername());
        User newUser = userService.createUser(userRequestDto);
        profileService.createEmpty(newUser);
        return ResponseEntity.ok("Utilisateur créé");
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody @Valid AuthenticationRequest request) {
        try {
            log.info("Tentative de connexion pour l'utilisateur : {}", request.getUsername());

            // Vérifier si l'utilisateur existe
            try {
                UserDetails userDetails = userDetailsService.loadUserByUsername(request.getUsername());
                log.info("Utilisateur trouvé dans la base de données : {}", userDetails.getUsername());
            } catch (Exception e) {
                log.error("Utilisateur introuvable dans la base de données : {}", request.getUsername());
                return ResponseEntity.status(401).body("Utilisateur non trouvé");
            }

            // Tenter l'authentification
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword()));

            log.info("Authentification réussie pour : {}", request.getUsername());

            final UserDetails userDetails = userDetailsService.loadUserByUsername(request.getUsername());
            final String jwt = jwtUtil.generateToken(userDetails);

            // Récupérer les détails de l'utilisateur pour la réponse
            com.example.genealogie.model.User user = userService.getUserByUsername(request.getUsername());

            return ResponseEntity.ok(new AuthenticationResponse(
                    jwt, user.getId(), user.getUsername(), user.getEmail(), user.getRole().name()));

        } catch (BadCredentialsException e) {
            log.error("Identifiants incorrects pour l'utilisateur : {}", request.getUsername());
            return ResponseEntity.status(401).body("Identifiants incorrects");
        } catch (Exception e) {
            log.error("Échec de connexion pour l'utilisateur : {}, erreur : {}", request.getUsername(), e.getMessage());
            return ResponseEntity.status(401).body("Erreur d'authentification");
        }
    }
}