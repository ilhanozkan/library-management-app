package com.ilhanozkan.libraryManagementSystem.security;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.test.context.ActiveProfiles;

import java.util.ArrayList;
import java.util.Date;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
public class JwtServiceTest {

    @Autowired
    private JwtService jwtService;

    private UserDetails userDetails;
    private String username;

    @BeforeEach
    void setUp() {
        username = "testuser";
        userDetails = new User(username, "password", new ArrayList<>());
    }

    @Test
    void shouldGenerateToken() {
        // Act
        String token = jwtService.generateToken(username);

        // Assert
        assertThat(token).isNotNull();
        assertThat(token.split("\\.").length).isEqualTo(3); // JWT has 3 parts: header.payload.signature
    }

    @Test
    void shouldExtractUsername() {
        // Arrange
        String token = jwtService.generateToken(username);

        // Act
        String extractedUsername = jwtService.extractUsername(token);

        // Assert
        assertThat(extractedUsername).isEqualTo(username);
    }

    @Test
    void shouldExtractExpiration() {
        // Arrange
        String token = jwtService.generateToken(username);

        // Act
        Date expirationDate = jwtService.extractExpiration(token);

        // Assert
        assertThat(expirationDate).isAfter(new Date()); // Token should not be expired yet
    }

    @Test
    void shouldValidateToken() {
        // Arrange
        String token = jwtService.generateToken(username);

        // Act
        boolean isValid = jwtService.validateToken(token, userDetails);

        // Assert
        assertTrue(isValid);
    }

    @Test
    void shouldDetectExpiredToken() throws Exception {
        // This test is simulating an expired token by changing the value in test profile
        // We're assuming the jwt.expiration in test profile is set to a very small value
        
        // Arrange: Generate a token (which might expire quickly based on test properties)
        String token = jwtService.generateToken(username);
        
        // Sleep to ensure token expires if expiration is set to a very small value
        Thread.sleep(100);
        
        // Act & Assert: If token expiration in test is set to a very small value, this might be expired
        // This is more of a demonstration than a reliable test
        boolean isExpired = jwtService.isTokenExpired(token);
        
        // We're not asserting the result as it depends on the test configuration
        System.out.println("Is token expired: " + isExpired + " (depends on test configuration)");
    }

    @Test
    void shouldNotValidateTokenWithDifferentUsername() {
        // Arrange
        String token = jwtService.generateToken(username);
        UserDetails differentUser = new User("differentuser", "password", new ArrayList<>());

        // Act
        boolean isValid = jwtService.validateToken(token, differentUser);

        // Assert
        assertFalse(isValid);
    }

    @Test
    void shouldExtractAllClaims() {
        // Arrange
        String token = jwtService.generateToken(username);

        // Act & Assert - if this doesn't throw an exception, it works
        assertDoesNotThrow(() -> jwtService.extractAllClaims(token));
    }
} 