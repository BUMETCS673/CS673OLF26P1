// AI-USAGE SUMMARY
// Tools: Gemini
// Overall AI Contribution: ~70%
// AI-Assisted Areas: JJWT parsing, signing, and key generation logic
// Human Contributions: Config properties declaration, explicit imports
// Notes: Token provider utility class strictly wrapped at 80 characters.
// Authors: Sara Orion

package edu.bu.metcs673.bluejay.auth.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.List;

// AI-ASSISTED: YES
// Tool: Gemini
// Prompt Summary: "Create JwtTokenProvider implementing JJWT token generation"
// AI Contribution: Core JWT construction logic (~75%)
// Modifications:
//   - Wrapped parameter lists and methods to conform to CS112.xml rules
// Verification:
//   - Verified compatibility with JJWT 0.12+ parser API
// Confidence: High
@Component
public class JwtTokenProvider {

    private final SecretKey key;
    @Getter
    private final long expirationMs;

    public JwtTokenProvider(
        @Value("${app.jwt.secret:BluejayPOSSecretKeyMustBeAtLeastThirtyTwoBytesLong!}")
        String secret,
        @Value("${app.jwt.expiration-ms:86400000}")
        long expirationMs) {
        this.key = Keys.hmacShaKeyFor(
            secret.getBytes(StandardCharsets.UTF_8)
        );
        this.expirationMs = expirationMs;
    }

    public String generateToken(Authentication authentication) {
        String username = authentication.getName();
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + expirationMs);

        List<String> authorities = authentication.getAuthorities()
            .stream()
            .map(GrantedAuthority::getAuthority)
            .toList();

        return Jwts.builder()
            .subject(username)
            .claim("roles", authorities)
            .issuedAt(now)
            .expiration(expiryDate)
            .signWith(key)
            .compact();
    }

    public String getUsernameFromToken(String token) {
        Claims claims = Jwts.parser()
            .verifyWith(key)
            .build()
            .parseSignedClaims(token)
            .getPayload();

        return claims.getSubject();
    }

    public boolean validateToken(String token) {
        try {
            Jwts.parser()
                .verifyWith(key)
                .build()
                .parseSignedClaims(token);
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            return false;
        }
    }
}