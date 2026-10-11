/*
    AI-USAGE SUMMARY
    Tools: Github Copilot
    Overall AI Contribution: 100%
    AI-Assisted Areas: User response creation
    Human Contributions: None
    Notes: UserResponse record based on the database schema
    Authors: Italia Tran
*/

package edu.bu.metcs673.bluejay.auth.dto;

import java.time.LocalDateTime;
import java.util.UUID;

public record UserResponse(
    UUID id,
    String username,
    Boolean enabled,
    String role,
    LocalDateTime createdAt
) {}
