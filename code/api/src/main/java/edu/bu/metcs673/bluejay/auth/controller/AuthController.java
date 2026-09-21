// AI-USAGE SUMMARY
// Tools: Gemini, Claude
// Overall AI Contribution: ~70%
// AI-Assisted Areas: REST controller mapping, ResponseEntity wrapping with ApiResponse, logout endpoint
// Human Contributions: API endpoint route design (/api/v1/auth/login)
// Notes: Auth slice entry point returning standardized ApiResponse wrapper.
// Authors: Sara Orion, Krizma Nagi

package edu.bu.metcs673.bluejay.auth.controller;

import edu.bu.metcs673.bluejay.auth.dto.AuthResponse;
import edu.bu.metcs673.bluejay.auth.dto.LoginRequest;
import edu.bu.metcs673.bluejay.auth.security.JwtTokenProvider;
import edu.bu.metcs673.bluejay.auth.security.TokenBlacklistService;
import edu.bu.metcs673.bluejay.auth.service.AuthService;
import edu.bu.metcs673.bluejay.common.dto.ApiResponse;
import jakarta.validation.Valid;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

// AI-ASSISTED: YES
// Tool: Gemini
// Prompt Summary: "Update AuthController to wrap AuthResponse inside ApiResponse"
// AI Contribution: Controller annotation and response wrapping (~80%)
// Modifications:
//   - Formatted using CS112 parameter rules and explicit imports
// Verification:
//   - Verified REST response structure against global API standards
// Confidence: High
@RestController
@RequestMapping("/auth")
@NullMarked
public class AuthController {

    private static final String BEARER_PREFIX = "Bearer ";

    private final AuthService authService;
    private final TokenBlacklistService tokenBlacklistService;
    private final JwtTokenProvider jwtTokenProvider;

    public AuthController(
        AuthService authService,
        TokenBlacklistService tokenBlacklistService,
        JwtTokenProvider jwtTokenProvider) {
        this.authService = authService;
        this.tokenBlacklistService = tokenBlacklistService;
        this.jwtTokenProvider = jwtTokenProvider;
    }

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<AuthResponse>> login(
        @Valid @RequestBody LoginRequest loginRequest) {
        AuthResponse authResponse = authService.login(loginRequest);
        ApiResponse<AuthResponse> response = ApiResponse.success(
            authResponse,
            "Authentication successful"
        );
        return ResponseEntity.ok(response);
    }

    // AI-ASSISTED: YES
    // Tool: Claude
    // Prompt Summary: "Add logout endpoint that revokes the caller's JWT"
    // AI Contribution: Endpoint and revocation logic (~70%)
    // Modifications:
    //   - Reads the Bearer token from the Authorization header and only revokes it if JwtTokenProvider.validateToken() accepts it, so invalid strings never fill the blacklist
    //   - Always returns 200 "Logout successful" (even with a missing, invalid or expired token) so repeated or stale logouts do not produce errors on the frontend
    //   - Marked the header parameter @Nullable because the class is @NullMarked
    // Verification:
    //   - AuthControllerTest logout tests (valid token revoked, invalid token not revoked, missing header returns 200)
    //   - Manual curl test: a token returned 500 (accepted) before logout and 401 after logout
    // Confidence: High
    @PostMapping("/logout")
    public ResponseEntity<ApiResponse<Void>> logout(
        @RequestHeader(value = "Authorization", required = false)
        @Nullable String authorization) {
        if (authorization != null
            && authorization.startsWith(BEARER_PREFIX)) {
            String token = authorization.substring(BEARER_PREFIX.length());
            if (jwtTokenProvider.validateToken(token)) {
                tokenBlacklistService.revoke(token);
            }
        }
        ApiResponse<Void> response =
            ApiResponse.success(null, "Logout successful");
        return ResponseEntity.ok(response);
    }
}