// AI-USAGE SUMMARY
// Tools: Gemini
// Overall AI Contribution: ~70%
// AI-Assisted Areas: REST controller mapping, ResponseEntity wrapping with ApiResponse
// Human Contributions: API endpoint route design (/api/v1/auth/login)
// Notes: Auth slice entry point returning standardized ApiResponse wrapper.
// Authors: Sara Orion

package edu.bu.metcs673.bluejay.auth.controller;

import edu.bu.metcs673.bluejay.auth.dto.AuthResponse;
import edu.bu.metcs673.bluejay.auth.dto.LoginRequest;
import edu.bu.metcs673.bluejay.auth.service.AuthService;
import edu.bu.metcs673.bluejay.common.dto.ApiResponse;
import jakarta.validation.Valid;
import org.jspecify.annotations.NullMarked;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
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
@RequestMapping("/api/v1/auth")
@NullMarked
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
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
}