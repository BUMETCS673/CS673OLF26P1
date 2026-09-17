// AI-USAGE SUMMARY
// Tools: Gemini
// Overall AI Contribution: ~80%
// AI-Assisted Areas: WebMvcTest setup, MockMvc request building, JSON assertions
// Human Contributions: Custom ApiResponse wrapper assertions
// Notes: Controller slice test for AuthController endpoints.
// Authors: Sara Orion

package edu.bu.metcs673.bluejay.auth.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import edu.bu.metcs673.bluejay.auth.dto.AuthResponse;
import edu.bu.metcs673.bluejay.auth.dto.LoginRequest;
import edu.bu.metcs673.bluejay.auth.security.JwtTokenProvider;
import edu.bu.metcs673.bluejay.auth.service.AuthService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AuthController.class)
@AutoConfigureMockMvc(addFilters = false)
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @MockitoBean
    private AuthService authService;

    // Add these missing bean mocks so JwtAuthenticationFilter can initialize
    @MockitoBean
    private JwtTokenProvider jwtTokenProvider;

    @MockitoBean
    private UserDetailsService userDetailsService;

    @Nested
    @DisplayName("POST /api/v1/auth/login Tests")
    class LoginEndpoint {

        // AI-ASSISTED: YES
        // Tool: Gemini
        // Prompt Summary: "JUnit 5 MockMvc test verifying HTTP 200 and ApiResponse structure for login"
        // AI Contribution: Initial draft (~85%)
        // Modifications:
        //   - Matched jsonPath assertions with ApiResponse.success factory wrapper
        // Verification:
        //   - Executed local unit test runner
        // Confidence: High
        @Test
        @DisplayName("Should return 200 OK and AuthResponse payload on valid login")
        void login_ValidPayload_Returns200() throws Exception {
            // Given
            LoginRequest loginRequest = new LoginRequest("sara_orion", "Password123!");
            AuthResponse authResponse = AuthResponse.of("mocked.jwt.token", "sara_orion", 3600000L);

            when(authService.login(any(LoginRequest.class))).thenReturn(authResponse);

            // When / Then
            mockMvc.perform(post("/api/v1/auth/login")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("Authentication successful"))
                .andExpect(jsonPath("$.data.token").value("mocked.jwt.token"))
                .andExpect(jsonPath("$.data.username").value("sara_orion"))
                .andExpect(jsonPath("$.data.expiresIn").value(3600000L));

            verify(authService, times(1)).login(any(LoginRequest.class));
        }

        // AI-ASSISTED: YES
        // Tool: Gemini
        // Prompt Summary: "JUnit 5 MockMvc test checking 400 Bad Request on blank validation fields"
        // AI Contribution: Initial draft (~85%)
        // Modifications:
        //   - Configured @Valid validation trigger assertion on empty payload
        // Verification:
        //   - Executed local unit test runner
        // Confidence: High
        @Test
        @DisplayName("Should return 400 Bad Request when request body fails validation")
        void login_InvalidPayload_Returns400() throws Exception {
            // Given (Blank username and password)
            LoginRequest invalidRequest = new LoginRequest("", "");

            // When / Then
            mockMvc.perform(post("/api/v1/auth/login")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest());

            verifyNoInteractions(authService);
        }
    }
}