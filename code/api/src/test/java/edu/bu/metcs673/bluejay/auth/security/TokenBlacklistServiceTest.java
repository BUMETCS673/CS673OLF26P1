// AI-USAGE SUMMARY
// Tools: Claude
// Overall AI Contribution: ~70%
// AI-Assisted Areas: Unit tests for revoke, lookup and expiry purge
// Human Contributions: Chose the scenarios (revoked token, unknown token, expiry purge), reviewed the assertions against TokenBlacklistService's purge logic, and ran the suite with mvnw test
// Notes: Uses a mocked JwtTokenProvider to control the token lifetime.
// Authors: Krizma Nagi

package edu.bu.metcs673.bluejay.auth.security;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

// AI-ASSISTED: YES
// Tool: Claude
// Prompt Summary: "Create JUnit 5 tests for TokenBlacklistService covering revoke, lookup and expiry cleanup"
// AI Contribution: Initial draft (~70%)
// Modifications:
//   - Mocks JwtTokenProvider.getExpirationMs() so each test controls the token lifetime
//   - Uses plain Mockito mocks (no Spring context) so the tests run quickly
// Verification:
//   - Local execution via JUnit runner (mvnw test)
// Confidence: High
class TokenBlacklistServiceTest {

    private TokenBlacklistService serviceWithLifetime(long lifetimeMs) {
        JwtTokenProvider provider = mock(JwtTokenProvider.class);
        when(provider.getExpirationMs()).thenReturn(lifetimeMs);
        return new TokenBlacklistService(provider);
    }

    // AI-ASSISTED: YES
    // Tool: Claude
    // Prompt Summary: "Test that a revoked token is reported as revoked"
    // AI Contribution: Initial draft (~90%)
    // Modifications: None
    // Verification:
    //   - Local execution via JUnit runner (mvnw test)
    // Confidence: High
    @Test
    void revokedTokenIsReportedAsRevoked() {
        TokenBlacklistService service = serviceWithLifetime(86_400_000L);
        service.revoke("token-a");
        assertTrue(service.isRevoked("token-a"));
    }

    // AI-ASSISTED: YES
    // Tool: Claude
    // Prompt Summary: "Test that a token that was never revoked is not reported as revoked"
    // AI Contribution: Initial draft (~90%)
    // Modifications: None
    // Verification:
    //   - Local execution via JUnit runner (mvnw test)
    // Confidence: High
    @Test
    void unknownTokenIsNotRevoked() {
        TokenBlacklistService service = serviceWithLifetime(86_400_000L);
        assertFalse(service.isRevoked("never-seen"));
    }

    // AI-ASSISTED: YES
    // Tool: Claude
    // Prompt Summary: "Test that revoked tokens older than the token lifetime are purged"
    // AI Contribution: Initial draft (~90%)
    // Modifications:
    //   - Uses a 1 ms token lifetime and a 50 ms sleep so the first entry is older than the cutoff when the second token is revoked
    // Verification:
    //   - Local execution via JUnit runner (mvnw test)
    // Confidence: High
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