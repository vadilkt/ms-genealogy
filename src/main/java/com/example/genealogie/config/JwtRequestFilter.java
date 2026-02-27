package com.example.genealogie.config;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.filter.OncePerRequestFilter;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

@Component
@RequiredArgsConstructor
public class JwtRequestFilter extends OncePerRequestFilter {

    private static final Logger logger = LoggerFactory.getLogger(JwtRequestFilter.class);
    private final UserDetailsService userDetailsService;
    private final JwtUtil jwtUtil;
    private final AntPathMatcher pathMatcher = new AntPathMatcher();

    private static final List<String> PUBLIC_ENDPOINTS = Arrays.asList(
            "/api/auth/**");

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws ServletException, IOException {

        String requestPath = request.getRequestURI();

        if ("OPTIONS".equalsIgnoreCase(request.getMethod())) {
            chain.doFilter(request, response);
            return;
        }

        if (isPublicEndpoint(requestPath)) {
            logger.debug("Accès à un point de terminaison public : {}", requestPath);
            chain.doFilter(request, response);
            return;
        }

        final String authorizationHeader = request.getHeader("Authorization");
        if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
            logger.debug(
                    "Aucun jeton JWT trouvé dans les en-têtes de la requête pour le point de terminaison protégé : {}",
                    requestPath);
            unauthorized(response, "En-tête Authorization manquant ou invalide");
            return;
        }

        // Trim to remove accidental extra spaces
        String jwt = authorizationHeader.substring(7).trim();
        String username;

        try {
            username = jwtUtil.extractUsername(jwt);
        } catch (ExpiredJwtException eje) {
            logger.warn("Jeton JWT expiré pour la requête {} : {}", requestPath, eje.getMessage());
            unauthorized(response, "Jeton expiré");
            return;
        } catch (JwtException je) {
            logger.error("Jeton JWT invalide pour la requête {} : {}", requestPath, je.getMessage());
            unauthorized(response, "Jeton invalide");
            return;
        } catch (Exception e) {
            logger.error("Échec de l'analyse du JWT pour la requête {} : {}", requestPath, e.getMessage());
            unauthorized(response, "Jeton invalide");
            return;
        }

        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            UserDetails userDetails = this.userDetailsService.loadUserByUsername(username);
            if (jwtUtil.validateToken(jwt, userDetails)) {
                UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
                        userDetails, null, userDetails.getAuthorities());
                authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(authenticationToken);
                logger.info("Utilisateur authentifié : {}", username);
            } else {
                logger.warn("Échec de la validation du jeton JWT pour l'utilisateur : {}", username);
                unauthorized(response, "Jeton invalide");
                return;
            }
        }

        chain.doFilter(request, response);
    }

    private boolean isPublicEndpoint(String requestPath) {
        return PUBLIC_ENDPOINTS.stream().anyMatch(pattern -> pathMatcher.match(pattern, requestPath));
    }

    private void unauthorized(HttpServletResponse response, String message) throws IOException {
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setHeader("WWW-Authenticate", "Bearer");
        response.setContentType("application/json");
        response.getWriter().write("{\"error\":\"" + message + "\"}");
        response.getWriter().flush();
    }
}