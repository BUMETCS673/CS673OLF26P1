package edu.bu.metcs673.bluejay.auth.service;

import edu.bu.metcs673.bluejay.auth.dto.UserResponse;

import java.util.List;

public interface UserService {
    List<UserResponse> getAllUsers();
}
