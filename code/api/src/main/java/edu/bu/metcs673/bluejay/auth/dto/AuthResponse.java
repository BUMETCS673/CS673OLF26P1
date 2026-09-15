// AI-USAGE SUMMARY
// Tools: Gemini
// Overall AI Contribution: ~70%
// AI-Assisted Areas: Java 21 record definition, compact constructor for
// default values
// Human Contributions: Token payload specifications
// Notes: Returns authentication token metadata using a compact record
// constructor for defaults.
// Authors: Sara Orion

package edu.bu.metcs673.bluejay.auth.dto;

// AI-ASSISTED: YES
// Tool: Gemini
// Prompt Summary: "Create AuthResponse DTO as a Java 21 record with default
// tokenType"
// AI Contribution: Java record declaration and compact constructor (~80%)
// Modifications:
//   - Utilized compact constructor to assign default 'Bearer' tokenType if null
// Verification:
//   - Verified record immutability and Spring REST Jackson serialization
// Confidence: High
public record AuthResponse(
    String token,
    String tokenType,
    String username,
    Long expiresIn
) {
    // Compact constructor to handle default values in Java 21 records
    public AuthResponse {
        if (tokenType == null) {
            tokenType = "Bearer";
        }
    }

    // Convenience constructor for simplified response instantiation
    public static AuthResponse of(String token,
        String username,
        Long expiresIn) {
        return new AuthResponse(token, "Bearer", username, expiresIn);
    }
}