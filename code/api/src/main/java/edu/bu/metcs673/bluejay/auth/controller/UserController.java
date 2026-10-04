/*
    AI-USAGE SUMMARY
    Tools: Github Copilot
    Overall AI Contribution: 90%
    AI-Assisted Areas: API creation and mapping
    Human Contributions: API authorization and status codes depending on if there is a response
    or if it fails
    Notes: User Controller class that primarily handles user related APIs
    Authors: Italia Tran
*/

package edu.bu.metcs673.bluejay.auth.controller;

import edu.bu.metcs673.bluejay.auth.dto.UserResponse;
import edu.bu.metcs673.bluejay.auth.service.UserService;
import edu.bu.metcs673.bluejay.common.dto.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataAccessException;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.transaction.TransactionException;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping("/users")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<List<UserResponse>>> getAllUsers() {
        try {
            ApiResponse<List<UserResponse>> response = ApiResponse.success(
                userService.getAllUsers(),
                "Users retrieved successfully"
            );
            return ResponseEntity.ok(response);
        } catch (DataAccessException | TransactionException ex) {
            ApiResponse<List<UserResponse>> response = ApiResponse.error(
                "An unexpected internal error occurred",
                "INTERNAL_SERVER_ERROR"
            );
            return ResponseEntity.internalServerError().body(response);
        }
    }
}