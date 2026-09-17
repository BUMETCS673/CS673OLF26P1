// AI-USAGE SUMMARY
// Tools: Gemini
// Overall AI Contribution: ~85%
// AI-Assisted Areas: MockHttpServletRequest/Response setup, SecurityContext assertions
// Human Contributions: CS112 inline formatting compliance
// Notes: Unit test for JwtAuthenticationFilter verifying SecurityContext injection.
// Authors: Sara Orion

package edu.bu.metcs673.bluejay.auth.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;

import java.io.IOException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class JwtAuthenticationFilterTest {

    @Mock
    private JwtTokenProvider jwtTokenProvider;

    @Mock
    private UserDetailsService userDetailsService;

    @Mock
    private FilterChain filterChain;

    private JwtAuthenticationFilter jwtAuthenticationFilter;
    private MockHttpServletRequest request;
    private MockHttpServletResponse response;

    @BeforeEach
    void setUp() {
        jwtAuthenticationFilter = new JwtAuthenticationFilter(jwtTokenProvider, userDetailsService);
        request = new MockHttpServletRequest();
        response = new MockHttpServletResponse();
        SecurityContextHolder.clearContext();
    }

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
    }

    @Nested
    @DisplayName("Filter Internal Execution Tests")
    class DoFilterInternal {

        // AI-ASSISTED: YES
        // Tool: Gemini
        // Prompt Summary: "JUnit 5 test for JwtAuthenticationFilter setting SecurityContext on valid Bearer header"
        // AI Contribution: Initial draft (~85%)
        // Modifications:
        //   - Configured MockHttpServletRequest header set
        // Verification:
        //   - Local execution via JUnit runner
        // Confidence: High
        @Test
        @DisplayName("Should authenticate user and populate SecurityContext on valid Bearer token")
        void doFilterInternal_ValidToken_SetsSecurityContext() throws ServletException, IOException {
            // Given
            String token = "valid.jwt.token";
            String username = "sara_orion";
            request.addHeader("Authorization", "Bearer " + token);

            UserDetails userDetails = new User(username, "password", List.of());

            when(jwtTokenProvider.validateToken(token)).thenReturn(true);
            when(jwtTokenProvider.getUsernameFromToken(token)).thenReturn(username);
            when(userDetailsService.loadUserByUsername(username)).thenReturn(userDetails);

            // When
            jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

            // Then
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            assertNotNull(authentication);
            assertEquals(username, authentication.getName());
            verify(filterChain, times(1)).doFilter(request, response);
        }

        // AI-ASSISTED: YES
        // Tool: Gemini
        // Prompt Summary: "JUnit 5 test checking JwtAuthenticationFilter ignores missing or non-Bearer headers"
        // AI Contribution: Initial draft (~90%)
        // Modifications:
        //   - Added verification that SecurityContext remains empty
        // Verification:
        //   - Local execution via JUnit runner
        // Confidence: High
        @Test
        @DisplayName("Should skip authentication when Authorization header is missing")
        void doFilterInternal_MissingHeader_SkipsAuthentication() throws ServletException, IOException {
            // When
            jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

            // Then
            assertNull(SecurityContextHolder.getContext().getAuthentication());
            verifyNoInteractions(jwtTokenProvider);
            verifyNoInteractions(userDetailsService);
            verify(filterChain, times(1)).doFilter(request, response);
        }
    }
}