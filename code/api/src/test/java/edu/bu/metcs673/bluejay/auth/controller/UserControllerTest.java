/*
    AI-USAGE SUMMARY
    Tools: Github Copilot / Gemini
    Overall AI Contribution: 90%
    AI-Assisted Areas: Added MockMvc test cases for PATCH /api/v1/users/{userId} partial update endpoint
    Human Contributions: Verified Security rule expectations and response assertions
    Notes: Test class for the User Controller
    Authors: Italia Tran, Sara Orion
*/

package edu.bu.metcs673.bluejay.auth.controller;

import edu.bu.metcs673.bluejay.auth.dto.CreateUserRequest;
import edu.bu.metcs673.bluejay.auth.dto.UpdateUserRequest;
import edu.bu.metcs673.bluejay.auth.dto.UserResponse;
import edu.bu.metcs673.bluejay.auth.security.JwtTokenProvider;
import edu.bu.metcs673.bluejay.auth.security.TokenBlacklistService;
import edu.bu.metcs673.bluejay.auth.service.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Import;
import org.springframework.dao.DataAccessResourceFailureException;
import org.springframework.http.MediaType;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.security.core.userdetails.UserDetailsService;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(UserController.class)
@AutoConfigureMockMvc(addFilters = false)
@Import(UserControllerTest.MethodSecurityTestConfig.class)
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private UserService userService;

    @MockitoBean
    private JwtTokenProvider jwtTokenProvider;

    @MockitoBean
    private UserDetailsService userDetailsService;

    @MockitoBean
    private TokenBlacklistService tokenBlacklistService;

    @WithMockUser(roles = "ADMIN")
    @Test
    void getAllUsers_ReturnsUsersWithoutSensitiveFields() throws Exception {
        UUID userId = UUID.randomUUID();
        LocalDateTime createdAt = LocalDateTime.of(2026, 10, 2, 12, 0);
        when(userService.getAllUsers()).thenReturn(List.of(
            new UserResponse(userId, "sara_orion", true, "ROLE_ADMIN", createdAt)
        ));

        mockMvc.perform(get("/api/v1/users"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.success").value(true))
            .andExpect(jsonPath("$.message").value("Users retrieved successfully"))
            .andExpect(jsonPath("$.data[0].id").value(userId.toString()))
            .andExpect(jsonPath("$.data[0].username").value("sara_orion"))
            .andExpect(jsonPath("$.data[0].enabled").value(true))
            .andExpect(jsonPath("$.data[0].createdAt").exists())
            .andExpect(jsonPath("$.data[0].passwordHash").doesNotExist());

        verify(userService).getAllUsers();
    }

    @WithMockUser(roles = "USER")
    @Test
    void getAllUsers_NonAdmin_ReturnsForbidden() throws Exception {
        mockMvc.perform(get("/api/v1/users"))
            .andExpect(status().isForbidden());

        org.mockito.Mockito.verifyNoInteractions(userService);
    }

    @WithMockUser(roles = "ADMIN")
    @Test
    void createUser_AdminCreatesUser_ReturnsCreatedUserResponse() throws Exception {
        UUID userId = UUID.randomUUID();
        LocalDateTime createdAt = LocalDateTime.of(2026, 10, 2, 12, 0);
        CreateUserRequest request = new CreateUserRequest("new_user", "Password123!", true, "ROLE_CASHIER");

        when(userService.createUser(any(CreateUserRequest.class)))
            .thenReturn(new UserResponse(userId, "new_user", true, "ROLE_CASHIER", createdAt));

        mockMvc.perform(post("/api/v1/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"username\":\"new_user\",\"password\":\"Password123!\",\"enabled\":true,\"role\":\"ROLE_CASHIER\"}"))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.success").value(true))
            .andExpect(jsonPath("$.message").value("User created successfully"))
            .andExpect(jsonPath("$.data.id").value(userId.toString()))
            .andExpect(jsonPath("$.data.username").value("new_user"))
            .andExpect(jsonPath("$.data.enabled").value(true));

        verify(userService).createUser(any(CreateUserRequest.class));
    }

    @WithMockUser(roles = "ADMIN")
    @Test
    void updateUser_AdminUpdatesRoleAndStatus_ReturnsUpdatedUserResponse() throws Exception {
        UUID userId = UUID.randomUUID();
        LocalDateTime createdAt = LocalDateTime.of(2026, 10, 2, 12, 0);

        when(userService.updateUser(eq(userId), any(UpdateUserRequest.class)))
            .thenReturn(new UserResponse(userId, "existing_user", false, "ROLE_MANAGER", createdAt));

        mockMvc.perform(patch("/api/v1/users/" + userId)
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"role\":\"ROLE_MANAGER\",\"enabled\":false}"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.success").value(true))
            .andExpect(jsonPath("$.message").value("User updated successfully"))
            .andExpect(jsonPath("$.data.role").value("ROLE_MANAGER"))
            .andExpect(jsonPath("$.data.enabled").value(false));

        verify(userService).updateUser(eq(userId), any(UpdateUserRequest.class));
    }

    @WithMockUser(roles = "USER")
    @Test
    void updateUser_NonAdmin_ReturnsForbidden() throws Exception {
        String userId = UUID.randomUUID().toString();

        mockMvc.perform(patch("/api/v1/users/" + userId)
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"role\":\"ROLE_MANAGER\"}"))
            .andExpect(status().isForbidden());

        org.mockito.Mockito.verifyNoInteractions(userService);
    }

    @WithMockUser(roles = "ADMIN")
    @Test
    void updateUser_ServiceRejectsRole_ReturnsBadRequest() throws Exception {
        UUID userId = UUID.randomUUID();
        when(userService.updateUser(eq(userId), any(UpdateUserRequest.class)))
            .thenThrow(new IllegalArgumentException("Role not found: INVALID_ROLE"));

        mockMvc.perform(patch("/api/v1/users/" + userId)
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"role\":\"INVALID_ROLE\"}"))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.success").value(false))
            .andExpect(jsonPath("$.errorCode").value("USER_UPDATE_FAILED"))
            .andExpect(jsonPath("$.message").value("Role not found: INVALID_ROLE"));

        verify(userService).updateUser(eq(userId), any(UpdateUserRequest.class));
    }

    @WithMockUser(roles = "ADMIN")
    @Test
    void getAllUsers_DatabaseConnectionFails_ReturnsInternalServerError() throws Exception {
        when(userService.getAllUsers()).thenThrow(
            new DataAccessResourceFailureException("Database unavailable")
        );

        mockMvc.perform(get("/api/v1/users"))
            .andExpect(status().isInternalServerError())
            .andExpect(jsonPath("$.success").value(false))
            .andExpect(jsonPath("$.errorCode").value("INTERNAL_SERVER_ERROR"))
            .andExpect(jsonPath("$.message").value("An unexpected internal error occurred"));
    }

    @TestConfiguration
    @EnableMethodSecurity
    static class MethodSecurityTestConfig {
    }
}