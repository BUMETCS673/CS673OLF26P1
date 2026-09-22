// AI-USAGE SUMMARY
// Tools: Gemini
// Overall AI Contribution: ~50%
// AI-Assisted Areas: Service interface contract design
// Human Contributions: Business contract definition, DTO requirements
// Notes: Defines security operations for user authentication and initial
// onboarding.
// Authors: Sara Orion

package edu.bu.metcs673.bluejay.auth.service;

import edu.bu.metcs673.bluejay.auth.dto.AuthResponse;
import edu.bu.metcs673.bluejay.auth.dto.LoginRequest;

// AI-ASSISTED: YES
// Tool: Gemini
// Prompt Summary: "Define AuthService interface contract for login"
// AI Contribution: Initial draft (~60%)
// Modifications:
//   - Aligned methods with REST API request/response requirements
// Verification:
//   - Interface signature verification
// Confidence: High
public interface AuthService {
    AuthResponse login(LoginRequest request);
}