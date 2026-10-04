package edu.bu.metcs673.bluejay.auth.service.impl;

import edu.bu.metcs673.bluejay.auth.dto.UserResponse;
import edu.bu.metcs673.bluejay.auth.repository.UserRepository;
import edu.bu.metcs673.bluejay.auth.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

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
}
