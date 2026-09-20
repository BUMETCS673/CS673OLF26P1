// AI-USAGE SUMMARY
// Tools: Gemini, Claude
// Overall AI Contribution: ~70%
// AI-Assisted Areas: OncePerRequestFilter structure, SecurityContext insertion, revoked-token check
// Human Contributions: Bearer string extraction, explicit import optimization
// Notes: Intercepts request to load principal from token if valid and not revoked.
// Authors: Sara Orion, Krizma Nagi

package edu.bu.metcs673.bluejay.auth.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

// AI-ASSISTED: YES
// Tool: Gemini
// Prompt Summary: "Create JwtAuthenticationFilter for request interception"
// AI Contribution: Filter template and context setting (~75%)
// Modifications:
//   - Extracted constants and applied CS112 method parameter wrapping
//   - Feature 18 (Claude): added TokenBlacklistService so revoked tokens are rejected
// Verification:
//   - Tested against Spring Security FilterChain execution order
// Confidence: High
@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private static final String AUTHORIZATION_HEADER = "Authorization";
    private static final String BEARER_PREFIX = "Bearer ";

    private final JwtTokenProvider tokenProvider;
    private final UserDetailsService userDetailsService;
    private final TokenBlacklistService tokenBlacklistService;

    // AI-ASSISTED: YES
    // Tool: Gemini
    // Prompt Summary: "Analyze PR reviewer comment about early return in Spring Security JWT filter and provide refactored method"
    // AI Contribution: Code refactoring (~60%)
    // Modifications:
    //  - Replaced nested if block with a guard clause checking !StringUtils.hasText(token) || !tokenProvider.validateToken(token)
    //  - Added explicit return statement after filterChain.doFilter in the guard clause to prevent duplicate chain executions
    //  - Feature 18 (Claude): guard clause also skips tokens found in the blacklist
    // Verification:
    //  - Manual code walkthrough of filter flow
    //  - Security integration testing with valid, invalid, and missing JWT tokens
    // Confidence: High
    @Override
    protected void doFilterInternal(
        @NonNull HttpServletRequest request,
        @NonNull HttpServletResponse response,
        @NonNull FilterChain filterChain)
        throws ServletException, IOException {

        String token = parseJwt(request);

        if (!StringUtils.hasText(token)
            || !tokenProvider.validateToken(token)
            || tokenBlacklistService.isRevoked(token)) {
            filterChain.doFilter(request, response);
            return;
        }

        String username = tokenProvider.getUsernameFromToken(token);
        UserDetails userDetails = userDetailsService.loadUserByUsername(username);

        UsernamePasswordAuthenticationToken authentication =
            new UsernamePasswordAuthenticationToken(
                userDetails,
                null,
                userDetails.getAuthorities()
            );

        authentication.setDetails(
            new WebAuthenticationDetailsSource().buildDetails(request)
        );

        SecurityContextHolder.getContext().setAuthentication(authentication);

        filterChain.doFilter(request, response);
    }

    private String parseJwt(HttpServletRequest request) {
        String headerAuth = request.getHeader(AUTHORIZATION_HEADER);

        if (StringUtils.hasText(headerAuth)
            && headerAuth.startsWith(BEARER_PREFIX)) {
            return headerAuth.substring(BEARER_PREFIX.length());
        }

        return null;
    }
}