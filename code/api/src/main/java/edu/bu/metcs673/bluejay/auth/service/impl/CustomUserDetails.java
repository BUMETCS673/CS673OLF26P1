// AI-USAGE SUMMARY
// Tools: Gemini
// Overall AI Contribution: ~85%
// AI-Assisted Areas: Java 21 record implementation of UserDetails, authority mapping, JPA deconstruction
// Human Contributions: Custom authority prefix validation and primitive field mapping
// Notes: Immutable UserDetails record deconstructing JPA entity for Clean Architecture and thread safety.
// Authors: Sara Orion

package edu.bu.metcs673.bluejay.auth.service.impl;

import edu.bu.metcs673.bluejay.auth.entity.Permission;
import edu.bu.metcs673.bluejay.auth.entity.Role;
import edu.bu.metcs673.bluejay.auth.entity.User;
import org.jspecify.annotations.NonNull;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

// AI-ASSISTED: YES
// Tool: Gemini
// Prompt Summary: "Refactor CustomUserDetails record to deconstruct JPA User entity into primitive fields"
// AI Contribution: Immutable record deconstruction and authority normalization (~85%)
// Modifications:
//   - Removed live User JPA entity reference to prevent LazyInitializationException
//   - Added role prefix normalization check to handle DB seed format alignment
// Verification:
//   - Verified record field accessors against Spring Security authentication providers
// Confidence: High
public record CustomUserDetails(
    UUID userId,
    String username,
    String passwordHash,
    boolean enabled,
    Set<GrantedAuthority> authorities
) implements UserDetails {

    public CustomUserDetails(User user) {
        this(
            user.getId(),
            user.getUsername(),
            user.getPasswordHash(),
            Boolean.TRUE.equals(user.getEnabled()),
            extractAuthorities(user)
        );
    }

    @Override
    @NonNull
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    private static Set<GrantedAuthority> extractAuthorities(User user) {
        Set<GrantedAuthority> auths = new HashSet<>();
        for (Role role : user.getRoles()) {
            // Role name in DB is already "ROLE_ADMIN"
            String roleName = role.getName().startsWith("ROLE_")
                ? role.getName()
                : "ROLE_" + role.getName();
            auths.add(new SimpleGrantedAuthority(roleName));

            for (Permission permission : role.getPermissions()) {
                auths.add(new SimpleGrantedAuthority(permission.getName()));
            }
        }
        return auths;
    }

    @Override
    @NonNull
    public String getPassword() {
        return passwordHash;
    }

    @Override
    @NonNull
    public String getUsername() {
        return username;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return enabled;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return enabled;
    }
}