package com.ets.security;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class JwtTokenProviderTest {

    private JwtTokenProvider jwtTokenProvider;

    @BeforeEach
    void setUp() {
        jwtTokenProvider = new JwtTokenProvider();
        ReflectionTestUtils.setField(jwtTokenProvider, "jwtSecret", "404E635266556A586E3272357538782F413F4428472B4B6250645367566B5970");
        ReflectionTestUtils.setField(jwtTokenProvider, "jwtExpirationMs", 3600000L); // 1 hour
    }

    @Test
    void generateToken_ShouldReturnToken() {
        Authentication authentication = mock(Authentication.class);
        UserDetails userDetails = new User("testuser", "password", Collections.emptyList());
        when(authentication.getPrincipal()).thenReturn(userDetails);

        String token = jwtTokenProvider.generateToken(authentication);

        assertNotNull(token);
        assertFalse(token.isEmpty());
    }

    @Test
    void getUsernameFromToken_ShouldReturnUsername() {
        Authentication authentication = mock(Authentication.class);
        UserDetails userDetails = new User("testuser", "password", Collections.emptyList());
        when(authentication.getPrincipal()).thenReturn(userDetails);
        String token = jwtTokenProvider.generateToken(authentication);

        String username = jwtTokenProvider.getUsernameFromToken(token);

        assertEquals("testuser", username);
    }

    @Test
    void validateToken_ValidToken_ShouldReturnTrue() {
        Authentication authentication = mock(Authentication.class);
        UserDetails userDetails = new User("testuser", "password", Collections.emptyList());
        when(authentication.getPrincipal()).thenReturn(userDetails);
        String token = jwtTokenProvider.generateToken(authentication);

        boolean isValid = jwtTokenProvider.validateToken(token);

        assertTrue(isValid);
    }

    @Test
    void validateToken_InvalidToken_ShouldReturnFalse() {
        boolean isValid = jwtTokenProvider.validateToken("invalidTokenString");

        assertFalse(isValid);
    }
}
