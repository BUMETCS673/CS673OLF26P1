// AI-USAGE SUMMARY
// Tools: Gemini
// Overall AI Contribution: ~80%
// AI-Assisted Areas: Unit test scaffolding, Mockito setup, AuthenticationManager mocking
// Human Contributions: AuthResponse DTO contract validation
// Notes: Unit test suite for AuthServiceImpl testing login workflow in isolation.
// Authors: Sara Orion

package edu.bu.metcs673.bluejay.auth.service.impl;

import edu.bu.metcs673.bluejay.auth.dto.AuthResponse;
import edu.bu.metcs673.bluejay.auth.dto.LoginRequest;
import edu.bu.metcs673.bluejay.auth.security.JwtTokenProvider;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceImplTest {

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private JwtTokenProvider tokenProvider;

    @Mock
    private Authentication authentication;

    private AuthServiceImpl authService;

    @BeforeEach
    void setUp() {
        authService = new AuthServiceImpl(authenticationManager, tokenProvider);
    }

    @Nested
    @DisplayName("login Tests")
    class Login {

        // AI-ASSISTED: YES
        // Tool: Gemini
        // Prompt Summary: "JUnit 5 test verifying successful login and AuthResponse DTO creation"
        // AI Contribution: Initial draft (~85%)
        // Modifications:
        //   - Verified mapping against AuthResponse.of record/factory contract
        // Verification:
        //   - Executed local unit test runner
        // Confidence: High
        @Test
        @DisplayName("Should return valid AuthResponse when credentials are valid")
        void login_Success() {
            // Given
            LoginRequest loginRequest = new LoginRequest("user", "Password123!");
            String expectedToken = "mocked.jwt.token";
            long expectedExpiration = 3600000L;

            when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(authentication);
            when(tokenProvider.generateToken(authentication))
                .thenReturn(expectedToken);
            when(tokenProvider.getExpirationMs())
                .thenReturn(expectedExpiration);

            // When
            AuthResponse response = authService.login(loginRequest);

            // Then
            assertNotNull(response);
            assertEquals(expectedToken, response.token());
            assertEquals("user", response.username());
            assertEquals(expectedExpiration, response.expiresIn());

            verify(authenticationManager, times(1))
                .authenticate(any(UsernamePasswordAuthenticationToken.class));
            verify(tokenProvider, times(1)).generateToken(authentication);
            verify(tokenProvider, times(1)).getExpirationMs();
        }

        // AI-ASSISTED: YES
        // Tool: Gemini
        // Prompt Summary: "JUnit 5 test checking exception propagation when authentication fails"
        // AI Contribution: Initial draft (~90%)
        // Modifications:
        //   - Asserted that BadCredentialsException bubbles up directly without masking
        // Verification:
        //   - Executed local unit test runner
        // Confidence: High
        @Test
        @DisplayName("Should throw BadCredentialsException when authentication fails")
        void login_BadCredentials_ThrowsException() {
            // Given
            LoginRequest loginRequest = new LoginRequest("bad_user", "wrong_password");
            when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenThrow(new BadCredentialsException("Invalid username or password"));

            // When / Then
            assertThrows(
                BadCredentialsException.class,
                () -> authService.login(loginRequest)
            );

            verify(authenticationManager, times(1))
                .authenticate(any(UsernamePasswordAuthenticationToken.class));
            verifyNoInteractions(tokenProvider);
        }
    }
}