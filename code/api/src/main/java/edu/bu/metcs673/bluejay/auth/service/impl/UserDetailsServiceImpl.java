// AI-USAGE SUMMARY
// Tools: Gemini
// Overall AI Contribution: ~70%
// AI-Assisted Areas: UserDetailsService implementation, lambda expression handling
// Human Contributions: Custom exception mapping on lookup failure
// Notes: Spring Security user loader service adhering to CS112 standards.
// Authors: Sara Orion

package edu.bu.metcs673.bluejay.auth.service.impl;

import edu.bu.metcs673.bluejay.auth.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.NullMarked;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

// AI-ASSISTED: YES
// Tool: Gemini
// Prompt Summary: "Implement UserDetailsService loading user by username"
// AI Contribution: Service implementation (~80%)
// Modifications:
//   - Added @Transactional and explicit formatting for line limits
// Verification:
//   - Verified Spring Security bean registration and dependency injection
// Confidence: High
@Service
@NullMarked
@RequiredArgsConstructor
public class UserDetailsServiceImpl implements UserDetailsService {

    private final UserRepository userRepository;

    @Override
    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(String username)
        throws UsernameNotFoundException {
        return userRepository.findByUsernameWithRolesAndPermissions(username)
            .map(CustomUserDetails::new)
            .orElseThrow(() -> new UsernameNotFoundException(
                "User not found with username: " + username
            ));
    }
}