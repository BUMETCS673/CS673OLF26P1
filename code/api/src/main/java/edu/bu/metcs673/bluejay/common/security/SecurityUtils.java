// AI-USAGE SUMMARY
// Tools: Gemini
// Overall AI Contribution: 100%
// AI-Assisted Areas: Security utility extracting authenticated user UUID
// from CustomUserDetails.
// Human Contributions: Custom business logic, validation, security refactoring.
// Notes: Initial code generated via AI; requires student verification,
// refactoring, and testing before integration.
// authors: Sara Orion

package edu.bu.metcs673.bluejay.common.security;

import edu.bu.metcs673.bluejay.auth.service.impl.CustomUserDetails;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.util.UUID;

// AI-ASSISTED: YES
// Tool: Gemini
// Prompt Summary: "Extract authenticated user UUID from CustomUserDetails
// record in Spring SecurityContext."
// AI Contribution: Initial draft (~100%)
// Modifications: Added pattern matching for CustomUserDetails record
// extraction.
// Verification: SecurityContext unit test.
// Confidence: High
@Component
public class SecurityUtils {

    public UUID getCurrentUserId() {
        Authentication authentication =
            SecurityContextHolder.getContext().getAuthentication();

        if (authentication != null &&
            authentication.getPrincipal() instanceof CustomUserDetails userDetails) {
            return userDetails.userId();
        }
        throw new IllegalStateException(
            "No authenticated user found in security context");
    }
}