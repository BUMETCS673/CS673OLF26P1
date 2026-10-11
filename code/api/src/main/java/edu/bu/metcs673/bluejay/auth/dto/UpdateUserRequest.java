package edu.bu.metcs673.bluejay.auth.dto;

public record UpdateUserRequest(
    String role,
    Boolean enabled
) {}

