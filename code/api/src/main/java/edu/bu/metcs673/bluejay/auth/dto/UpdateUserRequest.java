// AI-USAGE SUMMARY
// Tools: Gemini
// Overall AI Contribution: 100%
// AI-Assisted Areas: Java 21 record definition
// Human Contributions: Request field specifications
// Authors: Sara Orion

package edu.bu.metcs673.bluejay.auth.dto;

public record UpdateUserRequest(
    String role,
    Boolean enabled
) {}

