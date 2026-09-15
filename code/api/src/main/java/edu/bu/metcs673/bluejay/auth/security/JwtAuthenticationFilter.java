// AI-USAGE SUMMARY
// Tools: Gemini
// Overall AI Contribution: ~70%
// AI-Assisted Areas: OncePerRequestFilter structure, SecurityContext insertion
// Human Contributions: Bearer string extraction, explicit import optimization
// Notes: Intercepts request to load principal from token if valid.
// Authors: Sara Orion

package edu.bu.metcs673.bluejay.auth.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.lang.NonNull;
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

    @Override
    protected void doFilterInternal(
        @NonNull HttpServletRequest request,
        @NonNull HttpServletResponse response,
        @NonNull FilterChain filterChain)
        throws ServletException, IOException {

        String token = parseJwt(request);

        if (StringUtils.hasText(token)
            && tokenProvider.validateToken(token)) {
            String username = tokenProvider.getUsernameFromToken(token);

            UserDetails userDetails =
                userDetailsService.loadUserByUsername(username);
            UsernamePasswordAuthenticationToken authentication =
                new UsernamePasswordAuthenticationToken(
                    userDetails,
                    null,
                    userDetails.getAuthorities()
                );

            authentication.setDetails(
                new WebAuthenticationDetailsSource()
                    .buildDetails(request)
            );

            SecurityContextHolder.getContext()
                .setAuthentication(authentication);
        }

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