package com.charity_management_system.config_tests;

import com.charity_management_system.dto.CaseDto;
import com.charity_management_system.model.Case;
import com.charity_management_system.service.CaseService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.context.SecurityContextImpl;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.io.File;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * This class contains unit tests for security configurations related to the charity management system.
 * It uses Spring's testing framework, particularly with mock web services (MockMvc), and verifies
 * access control, CORS, and password encoding functionalities.
 * Annotations used:
 * - @ExtendWith(SpringExtension.class): Integrates Spring with JUnit 5.
 * - @SpringBootTest: Loads the application context for integration tests.
 * - @AutoConfigureMockMvc: Enables MockMvc-based testing.
 */
@ExtendWith(SpringExtension.class)
@SpringBootTest
@AutoConfigureMockMvc
class SecurityConfigTests {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CaseService caseService;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private AuthenticationManager authenticationManager;

    /**
     * Test to verify that an authenticated user with a specific role (ROLE_REGULAR_USER)
     * can successfully create a case via the /api/v1/cases/createCase endpoint.
     *
     * @throws Exception if there's an error during request execution.
     */
    @Test
    @WithMockUser(username = "testUser")
    void createCaseEndpoint_createCase_authenticatedUser() throws Exception {
        // Sets up the SecurityContext with mock authentication for the user 'testUser'
        SecurityContextHolder.setContext(new SecurityContextImpl(
                new org.springframework.security.authentication.UsernamePasswordAuthenticationToken(
                        "testUser", "password",
                        List.of(new SimpleGrantedAuthority("ROLE_REGULAR_USER"))
                )
        ));

        // Mock the behavior of caseService
        Case mockCase = new Case();
        mockCase.setTitle("Test Case");
        mockCase.setDescription("Test Description");
        mockCase.setGoal(10000);
        Mockito.when(caseService.createCase(Mockito.any(CaseDto.class), Mockito.anyString(), Mockito.any(File.class)))
                .thenReturn(mockCase);

        // Prepare a mock multipart file (image) and case DTO
        MockMultipartFile imageFile = new MockMultipartFile(
                "image",
                "testImage.jpg",
                MediaType.IMAGE_JPEG_VALUE,
                "testImageContent".getBytes()
        );

        CaseDto caseDto = new CaseDto();
        caseDto.setGoal(10000);
        caseDto.setTitle("Test Case");
        caseDto.setDescription("Test Description");

        // Perform an authenticated request and verify the response
        mockMvc.perform(MockMvcRequestBuilders.multipart("/api/v1/cases/createCase")
                        .file(imageFile)
                        .param("categoryName", "Education")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(caseDto))
                        .with(SecurityMockMvcRequestPostProcessors.csrf())
                        .with(user("testUser"))
                )
                .andExpect(status().isOk())
                .andExpect(content().string("done creating case"))
                .andDo(print());
    }

    /**
     * Test to verify that an unauthenticated user is denied access to the /api/v1/cases/createCase endpoint.
     *
     * @throws Exception if there's an error during request execution.
     */
    @Test
    void createCaseEndpoint_accessDenied_unauthenticatedUser() throws Exception {
        // Mock case setup
        Case mockCase = new Case();
        mockCase.setTitle("Test Case");
        mockCase.setDescription("Test Description");
        mockCase.setGoal(10000);
        Mockito.when(caseService.createCase(Mockito.any(CaseDto.class), Mockito.anyString(), Mockito.any(File.class)))
                .thenReturn(mockCase);

        // Prepare a mock multipart file (image) and case DTO
        MockMultipartFile imageFile = new MockMultipartFile(
                "image",
                "testImage.jpg",
                MediaType.IMAGE_JPEG_VALUE,
                "testImageContent".getBytes()
        );

        CaseDto caseDto = new CaseDto();
        caseDto.setGoal(10000);
        caseDto.setTitle("Test Case");
        caseDto.setDescription("Test Description");

        // Perform a request without authentication and expect a 401 Unauthorized response
        mockMvc.perform(multipart("/api/v1/cases/createCase")
                        .file(imageFile)
                        .param("categoryName", "Education")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(caseDto))
                )
                .andExpect(status().isUnauthorized())
                .andDo(print());
    }

    /**
     * Test to verify that the public /api/v1/cases/showCases endpoint is accessible to any user.
     *
     * @throws Exception if there's an error during request execution.
     */
    @Test
    void publicEndpoints_allowAccess_anyUser() throws Exception {
        mockMvc.perform(get("/api/v1/cases/showCases"))
                .andExpect(status().isOk());
    }

    /**
     * Test to verify that the CORS filter allows requests from an allowed origin, in this case, http://localhost:4200.
     *
     * @throws Exception if there's an error during request execution.
     */
    @Test
    void corsFilter_allowCorsRequests_allowedOrigin() throws Exception {
        mockMvc.perform(options("/api/v1/cases/createCase")
                        .header(HttpHeaders.ORIGIN, "http://localhost:4200")
                        .header(HttpHeaders.ACCESS_CONTROL_REQUEST_METHOD, "POST")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(header().exists(HttpHeaders.ACCESS_CONTROL_ALLOW_ORIGIN))
                .andExpect(header().string(HttpHeaders.ACCESS_CONTROL_ALLOW_ORIGIN, "http://localhost:4200"));
    }

    /**
     * Test to verify that the PasswordEncoder correctly encodes a raw password and that the encoded password matches the raw password.
     */
    @Test
    void passwordEncoder_encodePassword_plainPassword() {
        String rawPassword = "password";
        String encodedPassword = passwordEncoder.encode(rawPassword);

        assertTrue(passwordEncoder.matches(rawPassword, encodedPassword));
    }

    /**
     * Test to verify that the AuthenticationManager bean is correctly configured and available.
     */
    @Test
    void authenticationManagerBean_isConfigured() {
        assertNotNull(authenticationManager);
    }
}
