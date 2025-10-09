package com.example.genealogie.config;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

/**
 * Utility for generating and validating JWTs.
 *
 * Notes:
 * - To prevent signature mismatches, we always use byte[] secrets (not raw Strings)
 *   and normalize the configured secret (trim and optional Base64 decoding).
 * - If you store the secret as Base64, set client.secret.jwt-base64=true.
 */
@Component
public class JwtUtil {

    @Value("${client.secret.jwt}")
    private String SECRET_KEY;

    // Set to true if client.secret.jwt is Base64-encoded (recommended for long secrets)
    @Value("${client.secret.jwt-base64:false}")
    private boolean SECRET_BASE64;

    @Value("${jwt.token.validity}")
    private long JWT_TOKEN_VALIDITY; // expected in milliseconds

    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    public Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    private Claims extractAllClaims(String token) {
        return Jwts.parser()
                .setSigningKey(getSecretBytes())
                .parseClaimsJws(token)
                .getBody();
    }

    private Boolean isTokenExpired(String token) {
        final Date expiration = extractExpiration(token);
        return expiration.before(new Date());
    }

    public String generateToken(UserDetails userDetails) {
        Map<String, Object> claims = new HashMap<>();
        return createToken(claims, userDetails.getUsername());
    }

    private String createToken(Map<String, Object> claims, String subject) {
        final long now = System.currentTimeMillis();
        return Jwts.builder()
                .setClaims(claims)
                .setSubject(subject)
                .setIssuedAt(new Date(now))
                .setExpiration(new Date(now + JWT_TOKEN_VALIDITY))
                // Important: use byte[] key to ensure the same material is used for signing and verifying
                .signWith(SignatureAlgorithm.HS512, getSecretBytes())
                .compact();
    }

    public Boolean validateToken(String token, UserDetails userDetails) {
        final String username = extractUsername(token);
        return (username.equals(userDetails.getUsername()) && !isTokenExpired(token));
    }

    private byte[] getSecretBytes() {
        // Normalize the configured secret: trim and decode if Base64-encoded
        final String normalized = SECRET_KEY == null ? "" : SECRET_KEY.trim();
        if (SECRET_BASE64) {
            return Base64.getDecoder().decode(normalized);
        }
        return normalized.getBytes(StandardCharsets.UTF_8);
    }
}