package com.example.genealogie.controllers;

import com.example.genealogie.entities.User;
import com.example.genealogie.services.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
    private static final Logger logger = LoggerFactory.getLogger(AuthController.class);

    private UserService userService;
    private AuthenticationManager authenticationManager;

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping("/register")
    public User registrer(@RequestBody User user){
        logger.info("Tentative d'enregistrement de l'utilisateur: {}", user.getUsername());
        User savedUser = userService.save(user);
        logger.info("Utilisateur enregistré avec succès: {}", savedUser.getUsername());
        return savedUser;
    }

    @PostMapping("/login")
    public Response login(@RequestBody User user){
        logger.info("Tentative de connexion de l'utilisateur: {}", user.getUsername());

        try{
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(user.getUsername(), user.getPassword())
            );

            SecurityContextHolder.getContext().setAuthentication(authentication);
            logger.info("Connexion réussie pour l'utilisateur: {}", user.getUsername());

            return new Response("Connexion réussie", true);
        } catch (Exception e){
            logger.error("Connexion échouée pour l'utilisateur: {}", user.getUsername(),e);
            return new Response("Nom d'utilisateur ou mot de passe erroné", false);
        }
    }

    public static class Response{
        private String message;
        private boolean success;

        public Response(String message, boolean success){
            this.message = message;
            this.success = success;
        }
        public String getMessage(){
            return message;
        }
        public boolean isSuccess(){
            return success;
        }
    }
}
