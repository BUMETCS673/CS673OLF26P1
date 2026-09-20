// AI-USAGE SUMMARY
// Tools: Claude
// Overall AI Contribution: ~70%
// AI-Assisted Areas: In-memory JWT blacklist with expiry cleanup
// Human Contributions: (fill in what you changed and verified)
// Notes: Revoked tokens are dropped once they would have expired anyway.
// Authors: Krizma Nagi

package edu.bu.metcs673.bluejay.auth.security;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
@RequiredArgsConstructor
public class TokenBlacklistService {

    private final JwtTokenProvider jwtTokenProvider;
    private final Map<String, Instant> revokedTokens =
        new ConcurrentHashMap<>();

    public void revoke(String token) {
        purgeExpired();
        revokedTokens.put(token, Instant.now());
    }

    public boolean isRevoked(String token) {
        return revokedTokens.containsKey(token);
    }

    // A token revoked at time R cannot outlive R + expirationMs,
    // so older entries can be removed safely.
    private void purgeExpired() {
        Instant cutoff = Instant.now()
            .minusMillis(jwtTokenProvider.getExpirationMs());
        revokedTokens.values().removeIf(at -> at.isBefore(cutoff));
    }
}