// AI-USAGE SUMMARY
// Tools: Gemini
// Overall AI Contribution: ~70%
// AI-Assisted Areas: AuthenticationManager logic, JWT token generation call
// Human Contributions: CS112 line wrapping, Exception spec handling
// Notes: Handles credential validation and returns AuthResponse DTO.
// Authors: Sara Orion

package edu.bu.metcs673.bluejay.auth.service.impl;

import edu.bu.metcs673.bluejay.auth.dto.AuthResponse;
import edu.bu.metcs673.bluejay.auth.dto.LoginRequest;
import edu.bu.metcs673.bluejay.auth.security.JwtTokenProvider;
import edu.bu.metcs673.bluejay.auth.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

// AI-ASSISTED: YES
// Tool: Gemini
// Prompt Summary: "Implement AuthService for authentication and JWT generation"
// AI Contribution: Service logic implementation (~75%)
// Modifications:
//   - Structured to strictly adhere to CS112 indentation and 80-char line margins
// Verification:
//   - Tested authentication workflow against Spring Security DaoAuthenticationProvider
// Confidence: High
@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider tokenProvider;

    @Override
    public AuthResponse login(LoginRequest loginRequest) {
        Authentication authentication = authenticationManager.authenticate(
            new UsernamePasswordAuthenticationToken(
                loginRequest.username(),
                loginRequest.password()
            )
        );

        String token = tokenProvider.generateToken(authentication);
        long expiresIn = tokenProvider.getExpirationMs();

        return AuthResponse.of(
            token,
            loginRequest.username(),
            expiresIn
        );
    }
}