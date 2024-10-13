package com.charity_management_system.config_tests;

import io.jsonwebtoken.Claims;
import com.charity_management_system.config.JwtService;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.test.util.ReflectionTestUtils;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

/**
 * Unit tests for the {@link JwtService} class using Mockito.
 * This test class verifies the behavior of JWT (JSON Web Token) generation, validation, and extraction of claims.
 *
 * <p>Mocks are used to simulate the behavior of dependent classes, particularly {@link UserDetails}.</p>
 *
 * <p>This class uses the {@link MockitoExtension} to enable mock injection and simplify testing.</p>
 *
 * <p>Each test method covers different aspects of JWT token handling, such as token generation,
 * validating tokens, handling expiration, and extracting claims.</p>
 */
@ExtendWith(MockitoExtension.class)
class JwtServiceTests {

    /**
     * Mocked {@link UserDetails} used to simulate user authentication details for token generation.
     */
    @Mock
    private UserDetails userDetails;

    /**
     * The {@link JwtService} instance under test, with dependencies injected via {@link InjectMocks}.
     */
    @InjectMocks
    private JwtService jwtService;

    /**
     * Secret key used for signing the JWT.
     */
    private SecretKey signingKey;

    /**
     * Sample secret key for testing token generation.
     */
    private String secretKey = "mysecretkeyformocktestingmysecretkeyformocktesting";

    /**
     * Setup method executed before each test.
     * Configures the secret key and JWT expiration time for the {@link JwtService}.
     * Initializes the {@code signingKey} using the provided secret key.
     */
    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(jwtService, "secretKey", secretKey);
        ReflectionTestUtils.setField(jwtService, "jwtExpiration", 1000 * 60 * 60);
        byte[] keyBytes = Decoders.BASE64.decode(secretKey);
        signingKey = Keys.hmacShaKeyFor(keyBytes);
    }

    /**
     * Test for successfully generating a JWT token.
     * Verifies that the token is generated and that the username can be correctly extracted from it.
     */
    @Test
    void shouldGenerateTokenSuccessfully() {
        when(userDetails.getUsername()).thenReturn("testUser");

        String token = jwtService.generateToken(userDetails);

        assertNotNull(token, "Generated token should not be null");
        String extractedUsername = jwtService.extractUsername(token);
        assertEquals("testUser", extractedUsername, "Extracted username should match the provided username");
    }

    /**
     * Test for generating a JWT token with extra claims.
     * Verifies that additional claims (such as role) are correctly included in the token.
     */
    @Test
    void shouldIncludeExtraClaimsInToken() {
        when(userDetails.getUsername()).thenReturn("testUser");
        Map<String, Object> extraClaims = new HashMap<>();
        extraClaims.put("role", "ADMIN");

        String token = jwtService.generateToken(extraClaims, userDetails);

        Claims claims = Jwts.parserBuilder()
                .setSigningKey(signingKey)
                .build()
                .parseClaimsJws(token)
                .getBody();
        assertEquals("testUser", claims.getSubject());
        assertEquals("ADMIN", claims.get("role"));
    }

    /**
     * Test for validating a JWT token successfully.
     * Verifies that the token is valid for the provided user details.
     */
    @Test
    void shouldValidateTokenSuccessfully() {
        when(userDetails.getUsername()).thenReturn("testUser");
        String token = jwtService.generateToken(userDetails);

        boolean isValid = jwtService.isTokenValid(token, userDetails);

        assertTrue(isValid, "The token should be valid");
    }

    /**
     * Test for invalidating an expired JWT token.
     * Simulates token expiration by adjusting the expiration time and verifies that the token becomes invalid.
     */
    @Test
    void shouldInvalidateExpiredToken() throws InterruptedException {
        when(userDetails.getUsername()).thenReturn("testUser");
        ReflectionTestUtils.setField(jwtService, "jwtExpiration", 1000);
        String token = jwtService.generateToken(userDetails);
        Thread.sleep(1000); // Simulate token expiration by waiting

        boolean isValid = jwtService.isTokenValid(token, userDetails);

        assertFalse(isValid, "The token should be invalid due to expiration");
    }

    /**
     * Test for extracting the expiration date from a JWT token.
     * Verifies that the expiration date can be successfully extracted and that it is in the future.
     */
    @Test
    void shouldExtractExpirationSuccessfully() {
        when(userDetails.getUsername()).thenReturn("testUser");
        String token = jwtService.generateToken(userDetails);

        Date expirationDate = jwtService.extractClaim(token, Claims::getExpiration);

        assertNotNull(expirationDate, "Expiration date should not be null");
        assertTrue(expirationDate.after(new Date()), "Expiration date should be in the future");
    }
}

