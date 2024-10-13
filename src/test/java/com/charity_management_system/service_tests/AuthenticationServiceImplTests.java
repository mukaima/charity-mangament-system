package com.charity_management_system.service_tests;

import com.charity_management_system.config.JwtService;
import com.charity_management_system.dto.LoginRequest;
import com.charity_management_system.dto.LoginResponse;
import com.charity_management_system.enums.Role;
import com.charity_management_system.model.User;
import com.charity_management_system.repository.UserRepository;
import com.charity_management_system.service.impl.AuthenticationServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Unit tests for the {@link AuthenticationServiceImpl} class using Mockito.
 * This test class is responsible for verifying the behavior of the authentication service methods
 * such as user registration, login, and checking for existing usernames and emails.
 *
 * <p>Mocks are used to simulate the behavior of dependent classes such as {@link UserRepository},
 * {@link AuthenticationManager}, {@link PasswordEncoder}, and {@link JwtService}.</p>
 *
 * <p>This class uses the {@link MockitoExtension} to enable mock injection and simplify testing.</p>
 *
 * <p>Each test method is designed to cover specific scenarios, including successful and failed operations.</p>
 */
@ExtendWith(MockitoExtension.class)
class AuthenticationServiceImplTests {

    /**
     * Mocked {@link UserRepository} used to simulate user data persistence.
     */
    @Mock
    private UserRepository userRepository;

    /**
     * Mocked {@link AuthenticationManager} used to simulate user authentication.
     */
    @Mock
    private AuthenticationManager authenticationManager;

    /**
     * Mocked {@link PasswordEncoder} used to simulate password encoding functionality.
     */
    @Mock
    private PasswordEncoder passwordEncoder;

    /**
     * Mocked {@link JwtService} used to simulate JWT token generation.
     */
    @Mock
    private JwtService jwtService;

    /**
     * The {@link AuthenticationServiceImpl} instance under test, with dependencies injected via {@link InjectMocks}.
     */
    @InjectMocks
    private AuthenticationServiceImpl authenticationService;

    /**
     * Sample user object used in test cases.
     */
    private User testUser;

    /**
     * Setup method executed before each test.
     * Initializes the {@code testUser} object with basic information such as ID, username, password, and email.
     */
    @BeforeEach
    void setup(){
        testUser = new User();
        testUser.setId("some-id");
        testUser.setUsername("testUser");
        testUser.setPassword("password");
        testUser.setEmail("test@gmail.com");
    }

    /**
     * Test for registering a new user.
     * Verifies that the user is saved to the database without errors and that the password is correctly encoded.
     * Ensures the user's role is set to {@code Role.REGULAR_USER}.
     */
    @Test
    void register_saveUserToDatabase_noErrorsWhileSaving(){
        when(passwordEncoder.encode("password")).thenReturn("hashedPassword");
        when(userRepository.save(any(User.class))).thenReturn(testUser);

        String result = authenticationService.register(testUser);

        assertEquals("saved user successfully", result);
        verify(userRepository, times(1)).save(testUser);
        assertEquals("hashedPassword", testUser.getPassword());
        assertEquals(Role.REGULAR_USER, testUser.getRole());
    }

    /**
     * Test for handling a database error during user registration.
     * Verifies that the appropriate exception is thrown and that no user is saved when a runtime exception occurs.
     */
    @Test
    void register_throwException_databaseErrorWhileSaving(){
        when(passwordEncoder.encode("password")).thenReturn("hashedPassword");
        when(userRepository.save(any(User.class))).thenThrow(new RuntimeException("Database error"));

        String result = authenticationService.register(testUser);

        assertEquals("Database error", result);
        verify(userRepository, times(1)).save(testUser);
    }

    /**
     * Test for logging in a user.
     * Verifies that the user is authenticated and a JWT token is generated upon successful authentication.
     */
    @Test
    void login_authenticateAndGenerateToken_properAuthentication(){
        when(authenticationManager.authenticate(any())).thenReturn(null);
        when(userRepository.findByUsername("testUser")).thenReturn(Optional.of(testUser));
        when(jwtService.generateToken(any(User.class))).thenReturn("jwtToken");

        LoginRequest loginRequest = new LoginRequest("testUser", "password");
        LoginResponse loginResponse = authenticationService.login(loginRequest);

        assertNotNull(loginResponse);
        assertEquals("jwtToken", loginResponse.getToken());
        assertEquals(testUser.getUsername(), loginResponse.getUserProfile().getUsername());
        verify(authenticationManager, times(1)).authenticate(any());
        verify(userRepository, times(1)).findByUsername("testUser");
        verify(jwtService, times(1)).generateToken(testUser);
    }

    /**
     * Test for handling incorrect login credentials.
     * Verifies that a {@link BadCredentialsException} is thrown when authentication fails.
     */
    @Test
    void login_throwBadCredentialsException_falseAuthentication(){
        when(authenticationManager.authenticate(any())).thenThrow(new BadCredentialsException("Invalid username or password"));

        LoginRequest loginRequest = new LoginRequest("testUser", "wrongPassword");

        BadCredentialsException exception = assertThrows(BadCredentialsException.class, () -> {
            authenticationService.login(loginRequest);
        });

        assertEquals("Invalid username or password", exception.getMessage());
        verify(authenticationManager, times(1)).authenticate(any());
    }

    /**
     * Test for checking if a username exists in the database.
     * Verifies that the method returns {@code true} when the username is found.
     */
    @Test
    void checkUsername_returnTrue_usernameExists(){
        when(userRepository.existsByUsername("testUser")).thenReturn(true);

        Boolean result = authenticationService.checkUsername("testUser");

        assertTrue(result);
        verify(userRepository, times(1)).existsByUsername("testUser");
    }

    /**
     * Test for checking if a username does not exist in the database.
     * Verifies that the method returns {@code false} when the username is not found.
     */
    @Test
    void checkUsername_returnFalse_usernameDoesNotExist(){
        when(userRepository.existsByUsername("nonExistentUser")).thenReturn(false);

        Boolean result = authenticationService.checkUsername("nonExistentUser");

        assertFalse(result);
        verify(userRepository, times(1)).existsByUsername("nonExistentUser");
    }

    /**
     * Test for checking if an email exists in the database.
     * Verifies that the method returns {@code true} when the email is found.
     */
    @Test
    void checkEmail_returnTrue_emailExists(){
        when(userRepository.existsByEmail("test@gmail.com")).thenReturn(true);

        Boolean result = authenticationService.checkEmail("test@gmail.com");

        assertTrue(result);
        verify(userRepository, times(1)).existsByEmail("test@gmail.com");
    }

    /**
     * Test for checking if an email does not exist in the database.
     * Verifies that the method returns {@code false} when the email is not found.
     */
    @Test
    void checkEmail_returnFalse_emailDoesNotExist(){
        when(userRepository.existsByEmail("nonexistent@gmail.com")).thenReturn(false);

        Boolean result = authenticationService.checkEmail("nonexistent@gmail.com");

        assertFalse(result);
        verify(userRepository, times(1)).existsByEmail("nonexistent@gmail.com");
    }
}
