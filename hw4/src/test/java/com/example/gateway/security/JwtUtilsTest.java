package com.example.gateway.security;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class JwtUtilsTest {

    @Autowired
    private JwtUtils jwtUtils;

    private UsernamePasswordAuthenticationToken auth(String username, String... authorities) {
        return new UsernamePasswordAuthenticationToken(username, null,
                List.of(authorities).stream().map(SimpleGrantedAuthority::new).toList());
    }

    @Test
    void generateAndValidate_token() {
        String token = jwtUtils.generateToken(auth("user", "ROLE_USER"));
        assertThat(jwtUtils.validateToken(token)).isTrue();
    }

    @Test
    void getUsernameFromToken_returnsCorrectUsername() {
        String token = jwtUtils.generateToken(auth("testuser", "ROLE_USER"));
        assertThat(jwtUtils.getUsernameFromToken(token)).isEqualTo("testuser");
    }

    @Test
    void getAuthoritiesFromToken_returnsAuthorities() {
        String token = jwtUtils.generateToken(auth("reader", "ROLE_USER", "READ_PRIVILEGE"));
        assertThat(jwtUtils.getAuthoritiesFromToken(token)).contains("READ_PRIVILEGE");
    }

    @Test
    void validateToken_invalidToken_returnsFalse() {
        assertThat(jwtUtils.validateToken("invalid.token.here")).isFalse();
    }

    @Test
    void maskToken_masksCorrectly() {
        String token = "abcdef123456789xyz";
        String masked = jwtUtils.maskToken(token);
        assertThat(masked).startsWith("abcdef");
        assertThat(masked).endsWith("89xyz");
        assertThat(masked).contains("...");
        assertThat(masked).doesNotContain("123456");
    }
}
