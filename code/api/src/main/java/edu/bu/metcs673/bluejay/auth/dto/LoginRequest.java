// AI-USAGE SUMMARY
// Tools: Gemini
// Overall AI Contribution: ~70%
// AI-Assisted Areas: Java 21 record components, Jakarta validation tags
// Human Contributions: Field selection, error message specifications
// Notes: Uses native Java 21 record syntax for immutable data transfer.
// Authors: Sara Orion

package edu.bu.metcs673.bluejay.auth.dto;

import jakarta.validation.constraints.NotBlank;

// AI-ASSISTED: YES
// Tool: Gemini
// Prompt Summary: "Create LoginRequest DTO as a Java 21 record with Jakarta
// validation"
// AI Contribution: Java record definition and field-level validation (~80%)
// Modifications:
//   - Converted class to record type for Java 21 compliance
//   - Added explicit @NotBlank error messages
// Verification:
//   - Tested record component annotations against Spring Validation 3+
// Confidence: High
public record LoginRequest(
    @NotBlank(message = "Username is required")
    String username,

    @NotBlank(message = "Password is required")
    String password
) {
}