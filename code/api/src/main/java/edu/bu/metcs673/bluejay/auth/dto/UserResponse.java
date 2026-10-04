package edu.bu.metcs673.bluejay.auth.dto;

import java.time.LocalDateTime;
import java.util.UUID;

public record UserResponse(
    UUID id,
    String username,
    Boolean enabled,
    LocalDateTime createdAt
) {}
