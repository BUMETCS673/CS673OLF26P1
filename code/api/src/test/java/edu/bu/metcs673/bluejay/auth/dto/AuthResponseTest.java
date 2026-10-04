package edu.bu.metcs673.bluejay.auth.dto;

public final class AuthResponseTest {
    public final void testWithoutType() {
        final var ar = AuthResponse.of("token", "username", 3L);
        assert ar.tokenType().equals("Bearer");
        assert ar.token().equals("token");
        assert ar.expiresIn() == 3L;
        assert ar.username().equals("username");
    }


    public final void testWithType() {
        final var ar = new AuthResponse("token", "basic", "username", 3L);
        assert ar.tokenType().equals("basic");
        assert ar.token().equals("token");
        assert ar.username().equals("username");
        assert ar.expiresIn() == 3L;
    }
}