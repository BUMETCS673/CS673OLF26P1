/*
    AI-USAGE SUMMARY
    Tools: Github Copilot
    Overall AI Contribution: 100%
    AI-Assisted Areas: UserService interface creation
    Human Contributions: None
    Notes: UserService interface that will be used to get the UserResponse
    for anything related to the user table
    Authors: Italia Tran, Sara Orion
*/

package edu.bu.metcs673.bluejay.auth.service;

import edu.bu.metcs673.bluejay.auth.dto.CreateUserRequest;
import edu.bu.metcs673.bluejay.auth.dto.UpdateUserRequest;
import edu.bu.metcs673.bluejay.auth.dto.UserResponse;

import java.util.List;
import java.util.UUID;

public interface UserService {
    List<UserResponse> getAllUsers();

    UserResponse createUser(CreateUserRequest request);

    UserResponse updateUser(UUID userId, UpdateUserRequest request);
}
