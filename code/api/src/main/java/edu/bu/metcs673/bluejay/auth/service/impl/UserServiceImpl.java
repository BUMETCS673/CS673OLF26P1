/*
    AI-USAGE SUMMARY
    Tools: Github Copilot
    Overall AI Contribution: 100%
    AI-Assisted Areas: UserService implementation
    Human Contributions: None
    Notes: UserService implementation that is set by the UserService interface
    Authors: Italia Tran
*/

package edu.bu.metcs673.bluejay.auth.service.impl;

import edu.bu.metcs673.bluejay.auth.dto.CreateUserRequest;
import edu.bu.metcs673.bluejay.auth.dto.UpdateUserRequest;
import edu.bu.metcs673.bluejay.auth.dto.UserResponse;
import edu.bu.metcs673.bluejay.auth.entity.Role;
import edu.bu.metcs673.bluejay.auth.entity.User;
import edu.bu.metcs673.bluejay.auth.repository.RoleRepository;
import edu.bu.metcs673.bluejay.auth.repository.UserRepository;
import edu.bu.metcs673.bluejay.auth.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private static final String DEFAULT_ROLE_NAME = "ROLE_CASHIER";

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional(readOnly = true)
    public List<UserResponse> getAllUsers() {
        return userRepository.findAll().stream()
            .map(user -> new UserResponse(
                user.getId(),
                user.getUsername(),
                user.getEnabled(),
                user.getRolesAsString(),
                user.getCreatedAt()
            ))
            .toList();
    }

    @Override
    @Transactional
    public UserResponse createUser(CreateUserRequest request) {
        String username = request.username() == null
            ? ""
            : request.username().trim();
        String password = request.password() == null
            ? ""
            : request.password();
        String requestedRole = request.role() == null
            ? DEFAULT_ROLE_NAME
            : request.role().trim();

        if (username.isBlank()) {
            throw new IllegalArgumentException("Username is required");
        }
        if (password.isBlank()) {
            throw new IllegalArgumentException("Password is required");
        }

        if (userRepository.findByUsernameWithRolesAndPermissions(username).isPresent()) {
            throw new IllegalArgumentException("Username already exists");
        }

        String normalizedRole = requestedRole.isBlank()
            ? DEFAULT_ROLE_NAME
            : requestedRole;

        Role role = roleRepository.findByName(normalizedRole)
            .orElseGet(() -> roleRepository.findByName(DEFAULT_ROLE_NAME)
                .orElseThrow(() -> new IllegalArgumentException(
                    "Role not found: " + normalizedRole)));

        User newUser = User.builder()
            .username(username)
            .passwordHash(passwordEncoder.encode(password))
            .enabled(request.enabled() == null || request.enabled())
            .roles(Set.of(role))
            .build();

        User savedUser = userRepository.save(newUser);

        return new UserResponse(
            savedUser.getId(),
            savedUser.getUsername(),
            savedUser.getEnabled(),
            savedUser.getRolesAsString(),
            savedUser.getCreatedAt()
        );
    }

    // AI-ASSISTED: YES
    // Tool: Gemini
    // Prompt Summary: "Implement updateUser service method for updating role and enabled status partially by user ID."
    // AI Contribution: Initial draft (~85%)
    // Modifications: Added transactional scope, validation for non-existent users/roles, and conditional partial field mutation.
    // Verification: Unit test with Mockito and Spring Boot integration tests
    // Confidence: High
    @Override
    @Transactional
    public UserResponse updateUser(UUID userId, UpdateUserRequest request) {
        // 1. Find existing user
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new IllegalArgumentException("User not found with ID: " + userId));

        // 2. Partial Update: Account Enabled Status
        if (request.enabled() != null) {
            user.setEnabled(request.enabled());
        }

        // 3. Partial Update: Role Assignment
        if (request.role() != null && !request.role().isBlank()) {
            Role newRole = roleRepository.findByName(request.role())
                .orElseThrow(() -> new IllegalArgumentException("Role not found: " + request.role()));

            // Clear existing roles and add the new role to the managed collection
            user.getRoles().clear();
            user.getRoles().add(newRole);
        }

        // 4. Persist updated user
        User updatedUser = userRepository.save(user);

        // 5. Map updated entity to UserResponse DTO
        return new UserResponse( updatedUser.getId(), updatedUser.getUsername(),
            updatedUser.getEnabled(), updatedUser.getRolesAsString(),
            updatedUser.getCreatedAt() );

    }
}
