// AI-USAGE SUMMARY
// Tools: Gemini
// Overall AI Contribution: ~80%
// AI-Assisted Areas: JJWT parsing assertions, secret key setup, expiration verification
// Human Contributions: CS112 line wrapping compliance and claim validation checks
// Notes: Unit test suite for JwtTokenProvider using HMAC key generation.
// Authors: Sara Orion

package edu.bu.metcs673.bluejay.auth.security;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class JwtTokenProviderTest {

    private static final String MOCK_SECRET =
        "404E635266556A586E3272357538782F413F4428472B4B6250645367566B5970";
    private static final long MOCK_EXPIRATION_MS = 3600000L; // 1 hour

    private JwtTokenProvider jwtTokenProvider;

    @BeforeEach
    void setUp() {
        jwtTokenProvider = new JwtTokenProvider(MOCK_SECRET, MOCK_EXPIRATION_MS);
    }

    @Nested
    @DisplayName("Token Generation & Validation Tests")
    class TokenValidation {

        // AI-ASSISTED: YES
        // Tool: Gemini
        // Prompt Summary: "JUnit 5 test verifying JWT generation and username extraction"
        // AI Contribution: Initial draft (~85%)
        // Modifications:
        //   - Added Spring Security User principal mock setup
        // Verification:
        //   - Local execution via JUnit test runner
        // Confidence: High
        @Test
        @DisplayName("Should generate valid JWT token from Authentication principal")
        void generateToken_Success() {
            // Given
            User principal = new User(
                "user",
                "password",
                List.of(new SimpleGrantedAuthority("ROLE_USER"))
            );
            Authentication authentication = new UsernamePasswordAuthenticationToken(
                principal,
                null,
                principal.getAuthorities()
            );

            // When
            String token = jwtTokenProvider.generateToken(authentication);

            // Then
            assertNotNull(token);
            assertFalse(token.isBlank());
            assertTrue(jwtTokenProvider.validateToken(token));
            assertEquals("user", jwtTokenProvider.getUsernameFromToken(token));
        }

        // AI-ASSISTED: YES
        // Tool: Gemini
        // Prompt Summary: "JUnit 5 test checking validateToken behavior on malformed JWT"
        // AI Contribution: Initial draft (~90%)
        // Modifications:
        //   - Verified false return for malformed input strings
        // Verification:
        //   - Local execution via JUnit test runner
        // Confidence: High
        @Test
        @DisplayName("Should return false when validating a malformed token")
        void validateToken_MalformedToken_ReturnsFalse() {
            // Given
            String malformedToken = "invalid.jwt.string";

            // When
            boolean isValid = jwtTokenProvider.validateToken(malformedToken);

            // Then
            assertFalse(isValid);
        }

        // AI-ASSISTED: YES
        // Tool: Gemini
        // Prompt Summary: "JUnit 5 test checking expired token handling"
        // AI Contribution: Initial draft (~85%)
        // Modifications:
        //   - Initialized Provider with negative expiration interval to simulate expired state
        // Verification:
        //   - Local execution via JUnit test runner
        // Confidence: High
        @Test
        @DisplayName("Should return false when validating an expired token")
        void validateToken_ExpiredToken_ReturnsFalse() {
            // Given (Provider configured with -1000ms expiration)
            JwtTokenProvider expiredProvider = new JwtTokenProvider(MOCK_SECRET, -1000L);
            User principal = new User("user", "password", List.of());
            Authentication authentication = new UsernamePasswordAuthenticationToken(
                principal,
                null,
                principal.getAuthorities()
            );

            String expiredToken = expiredProvider.generateToken(authentication);

            // When
            boolean isValid = expiredProvider.validateToken(expiredToken);

            // Then
            assertFalse(isValid);
        }
    }
}