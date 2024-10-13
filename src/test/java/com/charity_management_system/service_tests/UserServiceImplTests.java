package com.charity_management_system.service_tests;

import com.charity_management_system.dto.UserProfile;
import com.charity_management_system.exception.custom.UserNotFoundException;
import com.charity_management_system.model.User;
import com.charity_management_system.repository.UserRepository;
import com.charity_management_system.service.impl.UserServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Unit tests for the {@link UserServiceImpl} class using Mockito.
 * This test class verifies the behavior of user-related methods, such as retrieving the user's account details.
 *
 * <p>Mocks are used to simulate the behavior of dependent classes such as {@link UserRepository}.</p>
 *
 * <p>This class uses the {@link MockitoExtension} to enable mock injection and simplify testing.</p>
 *
 * <p>Each test method is designed to cover specific scenarios, including both successful and failed operations.</p>
 */
@ExtendWith(MockitoExtension.class)
class UserServiceImplTests {

    /**
     * Mocked {@link UserRepository} used to simulate user data persistence.
     */
    @Mock
    private UserRepository userRepository;

    /**
     * The {@link UserServiceImpl} instance under test, with dependencies injected via {@link InjectMocks}.
     */
    @InjectMocks
    private UserServiceImpl userService;

    /**
     * Sample {@link User} object used in test cases.
     */
    private User mockUser;

    /**
     * Setup method executed before each test.
     * Initializes the sample user with username, email, an empty case list, and an empty donation list.
     */
    @BeforeEach
    void setup() {
        mockUser = new User();
        mockUser.setUsername("testUser");
        mockUser.setEmail("testUser@gmail.com");
        mockUser.setCaseList(new ArrayList<>());
        mockUser.setDonations(new ArrayList<>());
    }

    /**
     * Test for retrieving user account details when the user exists.
     * Verifies that the correct user profile is returned based on the username.
     */
    @Test
    void getUserAccount_returnUserProfile_userExists() {
        when(userRepository.findByUsername("testUser")).thenReturn(Optional.of(mockUser));

        UserProfile result = userService.getUserAccount("testUser");

        assertNotNull(result);
        assertEquals("testUser@gmail.com", result.getEmail());
        assertEquals("testUser", result.getUsername());
        assertEquals(mockUser.getCaseList(), result.getCaseList());
        assertEquals(mockUser.getDonations(), result.getDonations());

        verify(userRepository, times(1)).findByUsername("testUser");
    }

    /**
     * Test for retrieving user account details when the user does not exist.
     * Verifies that a {@link UserNotFoundException} is thrown for an invalid username.
     */
    @Test
    void getUserAccount_throwException_userDoesNotExist() {
        when(userRepository.findByUsername("testUser")).thenReturn(Optional.empty());

        UserNotFoundException exception = assertThrows(UserNotFoundException.class, () -> {
            userService.getUserAccount("testUser");
        });

        assertEquals("User Not Found With Username: testUser", exception.getMessage());

        verify(userRepository, times(1)).findByUsername("testUser");
    }
}

