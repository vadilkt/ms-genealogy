package com.example.genealogie.config;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

/**
 * Utility for generating and validating JWTs (JJWT 0.12+).
 *
 * Notes:
 * - To prevent signature mismatches, we use SecretKey from Keys.hmacShaKeyFor()
 *   and normalize the configured secret (trim and optional Base64 decoding).
 * - If you store the secret as Base64, set client.secret.jwt-base64=true.
 * - For HS512, the secret must be at least 64 bytes (512 bits). Use a long secret or Base64-encoded key.
 */
@Component
public class JwtUtil {

    @Value("${client.secret.jwt}")
    private String SECRET_KEY;

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
                .verifyWith(getSecretKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    private boolean isTokenExpired(String token) {
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
                .claims(claims)
                .subject(subject)
                .issuedAt(new Date(now))
                .expiration(new Date(now + JWT_TOKEN_VALIDITY))
                .signWith(getSecretKey())
                .compact();
    }

    public Boolean validateToken(String token, UserDetails userDetails) {
        final String username = extractUsername(token);
        return (username.equals(userDetails.getUsername()) && !isTokenExpired(token));
    }

    private static final int MIN_KEY_BYTES_HS512 = 64;

    private SecretKey getSecretKey() {
        final String normalized = SECRET_KEY == null ? "" : SECRET_KEY.trim();
        byte[] keyBytes;
        if (SECRET_BASE64) {
            keyBytes = Base64.getDecoder().decode(normalized);
        } else {
            keyBytes = normalized.getBytes(StandardCharsets.UTF_8);
        }
        if (keyBytes.length < MIN_KEY_BYTES_HS512) {
            keyBytes = java.util.Arrays.copyOf(keyBytes, MIN_KEY_BYTES_HS512);
        }
        return Keys.hmacShaKeyFor(keyBytes);
    }
}
