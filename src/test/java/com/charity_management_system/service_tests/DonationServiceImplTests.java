package com.charity_management_system.service_tests;

import com.charity_management_system.dto.DonationDto;
import com.charity_management_system.enums.PaymentMethod;
import com.charity_management_system.exception.custom.CaseNotFoundException;
import com.charity_management_system.exception.custom.UserNotFoundException;
import com.charity_management_system.model.Case;
import com.charity_management_system.model.Donation;
import com.charity_management_system.model.User;
import com.charity_management_system.repository.CaseRepository;
import com.charity_management_system.repository.DonationRepository;
import com.charity_management_system.repository.UserRepository;
import com.charity_management_system.service.impl.CommonService;
import com.charity_management_system.service.impl.DonationServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Unit tests for the {@link DonationServiceImpl} class using Mockito.
 * This test class verifies the behavior of donation-related methods, such as retrieving donations
 * by case ID, retrieving donations by user, and creating donations.
 *
 * <p>Mocks are used to simulate the behavior of dependent classes such as {@link DonationRepository},
 * {@link UserRepository}, {@link CaseRepository}, and {@link CommonService}.</p>
 *
 * <p>This class uses the {@link MockitoExtension} to enable mock injection and simplify testing.</p>
 *
 * <p>Each test method is designed to cover specific scenarios, including both successful and failed operations.</p>
 */
@ExtendWith(MockitoExtension.class)
class DonationServiceImplTests {

    /**
     * Mocked {@link DonationRepository} used to simulate donation data persistence.
     */
    @Mock
    private DonationRepository donationRepository;

    /**
     * Mocked {@link UserRepository} used to simulate user data persistence.
     */
    @Mock
    private UserRepository userRepository;

    /**
     * Mocked {@link CaseRepository} used to simulate case data persistence.
     */
    @Mock
    private CaseRepository caseRepository;

    /**
     * Mocked {@link CommonService} used to handle common conversion operations.
     */
    @Mock
    private CommonService commonService;

    /**
     * The {@link DonationServiceImpl} instance under test, with dependencies injected via {@link InjectMocks}.
     */
    @InjectMocks
    private DonationServiceImpl donationService;

    /**
     * Sample list of mock {@link Donation} objects used for test cases.
     */
    private List<Donation> mockDonations;

    /**
     * Sample {@link Case} object representing a case to which donations belong.
     */
    private Case mockCase;

    /**
     * Sample {@link DonationDto} object used to represent donation data for test cases.
     */
    private DonationDto donationDto;

    /**
     * Sample {@link User} object representing a user who makes donations.
     */
    private User user;

    /**
     * Sample {@link Case} object used to track donation amounts.
     */
    private Case donationCase;

    /**
     * Setup method executed before each test.
     * Initializes sample donation, user, and case data for use in the test cases.
     */
    @BeforeEach
    void setup(){
        mockCase = new Case();
        mockCase.setId(1);
        mockDonations = Arrays.asList(
                new Donation(1, 100.0, PaymentMethod.VODAFONE_CASH, mockCase, new User()),
                new Donation(2, 200.0, PaymentMethod.PAYPAL, mockCase, new User())
        );
        mockCase.setDonations(mockDonations);

        donationDto = new DonationDto(100.0, PaymentMethod.VODAFONE_CASH, 1);
        user = new User();
        user.setUsername("testUser");
        user.setDonations(mockDonations);

        donationCase = new Case();
        donationCase.setAmountRaised(500.0);
        donationCase.setDonations(new ArrayList<>());
    }

    /**
     * Test for retrieving donations by case ID.
     * Verifies that donations are returned when a valid case ID is provided.
     */
    @Test
    void getDonationsByCaseId_shouldReturnDonations_givenValidCaseId() {
        when(caseRepository.findById(1)).thenReturn(Optional.of(mockCase));

        List<Donation> result = donationService.getDonationsByCaseId(1);

        assertNotNull(result);
        assertEquals(2, result.size());
        verify(caseRepository, times(1)).findById(1);
    }

    /**
     * Test for retrieving donations by case ID when the case does not exist.
     * Verifies that a {@link CaseNotFoundException} is thrown for an invalid case ID.
     */
    @Test
    void getDonationsByCaseId_throwException_invalidCaseId() {
        int caseId = 90;
        when(caseRepository.findById(caseId)).thenReturn(Optional.empty());

        CaseNotFoundException exception = assertThrows(CaseNotFoundException.class, () -> {
            donationService.getDonationsByCaseId(caseId);
        });

        assertEquals("Case Not Found With Id: " + 90, exception.getMessage());
        verify(caseRepository, times(1)).findById(caseId);
    }

    /**
     * Test for retrieving donations by username when the user exists.
     * Verifies that donations are returned for the valid username.
     */
    @Test
    void getUserDonationsByUsername_returnDonations_validUsername() {
        when(userRepository.findByUsername("testUser")).thenReturn(Optional.of(user));
        when(commonService.convertDonationToDonationDTO(any(Donation.class))).thenReturn(new DonationDto());

        List<DonationDto> result = donationService.getUserDonationsByUsername("testUser");

        assertNotNull(result);
        assertEquals(2, result.size());
        verify(userRepository, times(1)).findByUsername("testUser");
        verify(commonService, times(2)).convertDonationToDonationDTO(any(Donation.class));
    }

    /**
     * Test for retrieving donations by username when the user does not exist.
     * Verifies that a {@link UserNotFoundException} is thrown for an invalid username.
     */
    @Test
    void getUserDonationsByUsername_throwException_invalidUsername() {
        when(userRepository.findByUsername("non-existent")).thenReturn(Optional.empty());

        UserNotFoundException exception = assertThrows(UserNotFoundException.class, () -> {
            donationService.getUserDonationsByUsername("non-existent");
        });

        assertEquals("user not found", exception.getMessage());
        verify(userRepository, times(1)).findByUsername("non-existent");
        verify(commonService, never()).convertDonationToDonationDTO(any(Donation.class));
    }

    /**
     * Nested class containing tests for creating donations.
     * These tests are run within a security context that simulates an authenticated user.
     */
    @Nested
    class MakeDonationTests {

        /**
         * Setup method executed before each test in the nested class.
         * Initializes the security context with an authenticated user.
         */
        @BeforeEach
        void setup(){
            SecurityContext securityContext = mock(SecurityContext.class);
            Authentication authentication = mock(Authentication.class);
            lenient().when(securityContext.getAuthentication()).thenReturn(authentication);
            lenient().when(authentication.getName()).thenReturn("testUser");
            SecurityContextHolder.setContext(securityContext);
        }

        /**
         * Test for creating a donation with valid data.
         * Verifies that the donation is created successfully and that the amount raised for the case is updated.
         */
        @Test
        void makeDonation_createDonation_validData() {
            when(userRepository.findByUsername("testUser")).thenReturn(Optional.of(user));
            when(caseRepository.findById(1)).thenReturn(Optional.of(donationCase));
            when(donationRepository.save(any(Donation.class))).thenAnswer(invocation -> invocation.getArgument(0));

            Donation result = donationService.makeDonation(donationDto);

            assertNotNull(result);
            assertEquals(600.0, donationCase.getAmountRaised());
            verify(donationRepository, times(1)).save(any(Donation.class));
            verify(caseRepository, times(1)).findById(1);
            verify(userRepository, times(1)).findByUsername("testUser");
        }

        /**
         * Test for creating a donation when the user is not found.
         * Verifies that a {@link UserNotFoundException} is thrown when the user does not exist in the system.
         */
        @Test
        void makeDonation_throwException_userNotFound() {
            when(userRepository.findByUsername("testUser")).thenReturn(Optional.empty());

            UserNotFoundException exception = assertThrows(UserNotFoundException.class, () -> {
                donationService.makeDonation(donationDto);
            });

            assertEquals("user not found", exception.getMessage());
            verify(userRepository, times(1)).findByUsername("testUser");
            verify(donationRepository, never()).save(any(Donation.class));
        }

        /**
         * Test for creating a donation when the case is not found.
         * Verifies that a {@link CaseNotFoundException} is thrown when the specified case does not exist.
         */
        @Test
        void makeDonation_throwException_caseNotFound() {
            when(userRepository.findByUsername("testUser")).thenReturn(Optional.of(user));
            when(caseRepository.findById(1)).thenReturn(Optional.empty());

            CaseNotFoundException exception = assertThrows(CaseNotFoundException.class, () -> {
                donationService.makeDonation(donationDto);
            });

            assertEquals("case not found", exception.getMessage());
            verify(caseRepository, times(1)).findById(1);
            verify(userRepository, times(1)).findByUsername("testUser");
            verify(donationRepository, never()).save(any(Donation.class));
        }
    }
}
