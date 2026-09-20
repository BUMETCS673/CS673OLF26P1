// AI-USAGE SUMMARY
// Tools: Claude
// Overall AI Contribution: ~70%
// AI-Assisted Areas: Unit tests for revoke, lookup and expiry purge
// Human Contributions: (fill in what you changed and verified)
// Notes: Uses a mocked JwtTokenProvider to control the token lifetime.
// Authors: Krizma Nagi

package edu.bu.metcs673.bluejay.auth.security;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class TokenBlacklistServiceTest {

    private TokenBlacklistService serviceWithLifetime(long lifetimeMs) {
        JwtTokenProvider provider = mock(JwtTokenProvider.class);
        when(provider.getExpirationMs()).thenReturn(lifetimeMs);
        return new TokenBlacklistService(provider);
    }

    @Test
    void revokedTokenIsReportedAsRevoked() {
        TokenBlacklistService service = serviceWithLifetime(86_400_000L);
        service.revoke("token-a");
        assertTrue(service.isRevoked("token-a"));
    }

    @Test
    void unknownTokenIsNotRevoked() {
        TokenBlacklistService service = serviceWithLifetime(86_400_000L);
        assertFalse(service.isRevoked("never-seen"));
    }

    @Test
    void entriesOlderThanTokenLifetimeArePurged()
        throws InterruptedException {
        TokenBlacklistService service = serviceWithLifetime(1L);
        service.revoke("old-token");
        Thread.sleep(50);
        service.revoke("new-token");
        assertFalse(service.isRevoked("old-token"));
        assertTrue(service.isRevoked("new-token"));
    }
}