// AI-USAGE SUMMARY
// Tools: Gemini
// Overall AI Contribution: ~70%
// AI-Assisted Areas: SecurityFilterChain, PasswordEncoder, AuthenticationManager beans
// Human Contributions: Public auth endpoint pattern matching, stateless session setup
// Notes: Wires JwtAuthenticationFilter prior to UsernamePasswordAuthenticationFilter.
// Authors: Sara Orion

package edu.bu.metcs673.bluejay.common.config;

import edu.bu.metcs673.bluejay.auth.security.JwtAuthenticationFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

// AI-ASSISTED: YES
// Tool: Gemini
// Prompt Summary: "Create SecurityConfig wiring JwtAuthenticationFilter in Spring Boot"
// AI Contribution: Security Bean definitions (~75%)
// Modifications:
//   - Configured route matching and CS112 indentations
// Verification:
//   - Tested permitting /api/v1/auth/** while securing protected routes
// Confidence: High
@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) {
        http
            .csrf(AbstractHttpConfigurer::disable)
            .sessionManagement(session ->
                session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .authorizeHttpRequests(auth -> auth
                // Permit the public health check endpoint
                .requestMatchers("/actuator/health").permitAll()
                // Permit your auth endpoints (register/login)
                .requestMatchers("/api/v1/auth/**").permitAll()
                // Secure all other routes
                .anyRequest().authenticated()
            )
            .addFilterBefore(
                jwtAuthenticationFilter,
                UsernamePasswordAuthenticationFilter.class
            );

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(
        AuthenticationConfiguration authenticationConfiguration) {
        return authenticationConfiguration.getAuthenticationManager();
    }
}