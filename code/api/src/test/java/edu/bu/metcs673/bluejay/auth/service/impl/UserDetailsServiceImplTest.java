// AI-USAGE SUMMARY
// Tools: Gemini
// Overall AI Contribution: ~80%
// AI-Assisted Areas: Unit test scaffolding, Mockito setup, CustomUserDetails assertions
// Human Contributions: Role and permission authority mapping assertions
// Notes: Unit test suite for UserDetailsServiceImpl and CustomUserDetails record.
// Authors: Sara Orion

package edu.bu.metcs673.bluejay.auth.service.impl;

import edu.bu.metcs673.bluejay.auth.entity.Permission;
import edu.bu.metcs673.bluejay.auth.entity.Role;
import edu.bu.metcs673.bluejay.auth.entity.User;
import edu.bu.metcs673.bluejay.auth.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserDetailsServiceImplTest {

    @Mock
    private UserRepository userRepository;

    private UserDetailsServiceImpl userDetailsService;

    @BeforeEach
    void setUp() {
        userDetailsService = new UserDetailsServiceImpl(userRepository);
    }

    @Nested
    @DisplayName("loadUserByUsername Tests")
    class LoadUserByUsername {

        // AI-ASSISTED: YES
        // Tool: Gemini
        // Prompt Summary: "JUnit 5 test verifying UserDetailsServiceImpl loads user and maps authorities correctly"
        // AI Contribution: Initial draft (~85%)
        // Modifications:
        //   - Added role and permission mapping checks matching CustomUserDetails record
        // Verification:
        //   - Executed local unit test runner
        // Confidence: High
        @Test
        @DisplayName("Should return CustomUserDetails with correct authorities when username exists")
        void loadUserByUsername_Success() {
            // Given
            String username = "sara_orion";

            Permission readUserPerm = new Permission();
            readUserPerm.setName("user:read");

            Role adminRole = new Role();
            adminRole.setName("ADMIN");
            adminRole.setPermissions(Set.of(readUserPerm));

            User mockUser = new User();
            mockUser.setUsername(username);
            mockUser.setPasswordHash("$2a$10$hashedPasswordValue");
            mockUser.setEnabled(true);
            mockUser.setRoles(Set.of(adminRole));

            when(userRepository.findByUsernameWithRolesAndPermissions(username))
                .thenReturn(Optional.of(mockUser));

            // When
            UserDetails userDetails = userDetailsService.loadUserByUsername(username);

            // Then
            assertNotNull(userDetails);
            assertInstanceOf(CustomUserDetails.class, userDetails);
            assertEquals(username, userDetails.getUsername());
            assertEquals("$2a$10$hashedPasswordValue", userDetails.getPassword());
            assertTrue(userDetails.isEnabled());

            // Verify Authorities (Role prefixing + permissions)
            Set<String> authorities = userDetails.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(java.util.stream.Collectors.toSet());

            assertTrue(authorities.contains("ROLE_ADMIN"));
            assertTrue(authorities.contains("user:read"));

            verify(userRepository, times(1)).findByUsernameWithRolesAndPermissions(username);
        }

        // AI-ASSISTED: YES
        // Tool: Gemini
        // Prompt Summary: "JUnit 5 test verifying UsernameNotFoundException on missing user"
        // AI Contribution: Initial draft (~90%)
        // Modifications:
        //   - Matched exception message with UserDetailsServiceImpl implementation
        // Verification:
        //   - Executed local unit test runner
        // Confidence: High
        @Test
        @DisplayName("Should throw UsernameNotFoundException when user is not found")
        void loadUserByUsername_NotFound_ThrowsException() {
            // Given
            String username = "missing_user";
            when(userRepository.findByUsernameWithRolesAndPermissions(username))
                .thenReturn(Optional.empty());

            // When / Then
            UsernameNotFoundException exception = assertThrows(
                UsernameNotFoundException.class,
                () -> userDetailsService.loadUserByUsername(username)
            );

            assertEquals("User not found with username: " + username, exception.getMessage());
            verify(userRepository, times(1)).findByUsernameWithRolesAndPermissions(username);
        }
    }
}