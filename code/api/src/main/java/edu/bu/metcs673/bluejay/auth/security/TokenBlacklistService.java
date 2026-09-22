// AI-USAGE SUMMARY
// Tools: Claude
// Overall AI Contribution: ~70%
// AI-Assisted Areas: In-memory JWT blacklist with expiry cleanup
// Human Contributions: Required revoked tokens to be purged once they would have expired, reviewed the blacklist logic against JwtTokenProvider's expiration setting, and verified it with unit tests and a manual logout test
// Notes: Revoked tokens are dropped once they would have expired anyway.
// Authors: Krizma Nagi

package edu.bu.metcs673.bluejay.auth.security;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

// AI-ASSISTED: YES
// Tool: Claude
// Prompt Summary: "Create an in-memory JWT blacklist service that drops revoked tokens once they would have expired"
// AI Contribution: Service structure and expiry cleanup logic (~70%)
// Modifications:
//   - Uses JwtTokenProvider.getExpirationMs() so cleanup follows the configured token lifetime
//   - Stores each revoked token with its revocation time in a ConcurrentHashMap so concurrent requests are safe
// Verification:
//   - TokenBlacklistServiceTest (revoke, unknown token, expiry purge)
//   - Manual curl test: a token returned 500 (accepted) before logout and 401 after logout
// Confidence: High
@Service
@RequiredArgsConstructor
public class TokenBlacklistService {

    private final JwtTokenProvider jwtTokenProvider;
    private final Map<String, Instant> revokedTokens =
        new ConcurrentHashMap<>();

    // AI-ASSISTED: YES
    // Tool: Claude
    // Prompt Summary: "Add a revoke method that blacklists a token and cleans up old entries"
    // AI Contribution: Revocation logic (~70%)
    // Modifications:
    //   - Purges expired entries before adding the new one so the map does not grow without bound
    // Verification:
    //   - TokenBlacklistServiceTest and AuthControllerTest logout tests
    // Confidence: High
    public void revoke(String token) {
        purgeExpired();
        revokedTokens.put(token, Instant.now());
    }

    // AI-ASSISTED: YES
    // Tool: Claude
    // Prompt Summary: "Add a method that checks whether a token has been revoked"
    // AI Contribution: Lookup logic (~80%)
    // Modifications: None
    // Verification:
    //   - TokenBlacklistServiceTest and JwtAuthenticationFilterTest (revoked token is not authenticated)
    // Confidence: High
    public boolean isRevoked(String token) {
        return revokedTokens.containsKey(token);
    }

    // AI-ASSISTED: YES
    // Tool: Claude
    // Prompt Summary: "Add cleanup that removes revoked tokens that would have expired anyway"
    // AI Contribution: Purge logic (~70%)
    // Modifications:
    //   - A token revoked at time R cannot outlive R + expirationMs, so older entries can be removed safely
    // Verification:
    //   - TokenBlacklistServiceTest (entries older than the token lifetime are purged)
    // Confidence: High
    private void purgeExpired() {
        Instant cutoff = Instant.now()
            .minusMillis(jwtTokenProvider.getExpirationMs());
        revokedTokens.values().removeIf(at -> at.isBefore(cutoff));
    }
}