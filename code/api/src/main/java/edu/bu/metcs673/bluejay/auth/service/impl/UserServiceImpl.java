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
            savedUser.getCreatedAt()
        );
    }
}
