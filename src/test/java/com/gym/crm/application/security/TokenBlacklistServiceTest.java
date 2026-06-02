package com.gym.crm.application.security;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class TokenBlacklistServiceTest {

    private final TokenBlacklistService service = new TokenBlacklistService();

    @Test
    void blacklist_shouldMarkTokenAsBlacklisted() {
        String token = "jwt-token";

        service.blacklist(token);

        assertThat(service.isBlacklisted(token)).isTrue();
    }

    @Test
    void isBlacklisted_shouldReturnFalseForUnknownToken() {
        assertThat(service.isBlacklisted("unknown-token")).isFalse();
    }
}