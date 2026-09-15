// AI-USAGE SUMMARY
// Tools: Gemini
// Overall AI Contribution: ~75%
// AI-Assisted Areas: Java 21 record implementation of UserDetails contract
// Human Contributions: Custom authority mapping and @NonNull annotations
// Notes: Immutable UserDetails record formatted for CS112 standards.
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

// AI-ASSISTED: YES
// Tool: Gemini
// Prompt Summary: "Refactor CustomUserDetails class into Java 21 record"
// AI Contribution: Record declaration and contract mapping (~80%)
// Modifications:
//   - Added @NonNull annotations for Spring 7 / Spring Boot 4 compliance
// Verification:
//   - Verified record accessor compatibility with Spring Security
// Confidence: High
public record CustomUserDetails(User user) implements UserDetails {

    @Override
    @NonNull
    public Collection<? extends GrantedAuthority> getAuthorities() {
        Set<GrantedAuthority> authorities = new HashSet<>();

        for (Role role : user.getRoles()) {
            authorities.add(
                new SimpleGrantedAuthority("ROLE_" + role.getName())
            );

            for (Permission permission : role.getPermissions()) {
                authorities.add(
                    new SimpleGrantedAuthority(permission.getName())
                );
            }
        }

        return authorities;
    }

    @Override
    @NonNull
    public String getPassword() {
        return user.getPasswordHash();
    }

    @Override
    @NonNull
    public String getUsername() {
        return user.getUsername();
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return Boolean.TRUE.equals(user.getEnabled());
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return Boolean.TRUE.equals(user.getEnabled());
    }
}