/*
    AI-USAGE SUMMARY
    Tools: GitHub Copilot
    Overall AI Contribution: 100%
    AI-Assisted Areas: Added the request payload for the user creation
    Human Contributions: None
    Notes: The request payload for the API
    Authors: Italia Tran
*/

package edu.bu.metcs673.bluejay.auth.dto;

import jakarta.validation.constraints.NotBlank;

public record CreateUserRequest(
    @NotBlank(message = "Username is required")
    String username,

    @NotBlank(message = "Password is required")
    String password,

    Boolean enabled,

    String role
) {
}
