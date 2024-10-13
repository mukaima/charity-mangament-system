package com.charity_management_system.service_tests;

import com.charity_management_system.dto.CaseDto;
import com.charity_management_system.dto.ImageSavingResponse;
import com.charity_management_system.enums.CaseStatus;
import com.charity_management_system.exception.custom.CaseNotFoundException;
import com.charity_management_system.exception.custom.CategoryNotFoundException;
import com.charity_management_system.exception.custom.UserNotFoundException;
import com.charity_management_system.model.Case;
import com.charity_management_system.model.Category;
import com.charity_management_system.model.User;
import com.charity_management_system.repository.CaseRepository;
import com.charity_management_system.repository.CategoryRepository;
import com.charity_management_system.repository.UserRepository;
import com.charity_management_system.service.impl.CaseServiceImpl;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.charity_management_system.service.impl.CommonService;
import com.google.api.services.drive.Drive;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Unit tests for the {@link CaseServiceImpl} class using Mockito.
 * This test class verifies the behavior of the case service methods such as creating, updating, searching,
 * and deleting cases, as well as retrieving cases by category and user.
 *
 * <p>Mocks are used to simulate the behavior of dependent classes such as {@link CaseRepository}, {@link UserRepository},
 * {@link CategoryRepository}, {@link CommonService}, and {@link Drive}.</p>
 *
 * <p>This class uses the {@link MockitoExtension} to enable mock injection and simplify testing.</p>
 *
 * <p>Each test method is designed to cover specific scenarios, including successful and failed operations.</p>
 */
@ExtendWith(MockitoExtension.class)
class CaseServiceImplTests {

    /**
     * Mocked {@link CaseRepository} used to simulate case data persistence.
     */
    @Mock
    private CaseRepository caseRepository;

    /**
     * Mocked {@link UserRepository} used to simulate user data persistence.
     */
    @Mock
    private UserRepository userRepository;

    /**
     * Mocked {@link CategoryRepository} used to simulate category data persistence.
     */
    @Mock
    private CategoryRepository categoryRepository;

    /**
     * Mocked {@link CommonService} used to simulate common service methods.
     */
    @Mock
    private CommonService commonService;

    /**
     * Mocked {@link Drive} used to simulate file storage operations.
     */
    @Mock
    private Drive drive;

    /**
     * Captor used to capture {@link Case} objects during test method execution.
     */
    @Captor
    private ArgumentCaptor<Case> caseCaptor;

    /**
     * The {@link CaseServiceImpl} instance under test, with dependencies injected via {@link InjectMocks}.
     */
    @InjectMocks
    private CaseServiceImpl caseService;

    /**
     * Sample {@link User} object used in test cases.
     */
    private User testUser;

    /**
     * Sample {@link Category} object used in test cases.
     */
    private Category testCategory;

    /**
     * Sample {@link CaseDto} object used in test cases.
     */
    private CaseDto testCaseDto;

    /**
     * Sample {@link File} object used for image testing.
     */
    private File testImage;

    /**
     * List of mock {@link Case} objects used in test cases.
     */
    private List<Case> mockCases = new ArrayList<>();

    /**
     * Sample {@link Case} object used in test cases.
     */
    private Case testCase;

    /**
     * Setup method executed before each test.
     * Initializes sample user, category, and case data, as well as mock cases for searching and other tests.
     */
    @BeforeEach
    void setup(){
        testUser = new User();
        testUser.setUsername("testUser");

        testCategory = new Category();
        testCategory.setName("Medical");
        testCategory.setCaseList(new ArrayList<>());

        mockCases = new ArrayList<>();
        Case case1 = new Case();
        case1.setTitle("Medical Help");
        case1.setDescription("Need funds for surgery.");
        mockCases.add(case1);

        Case case2 = new Case();
        case2.setTitle("Education Support");
        case2.setDescription("Help for college fees.");
        mockCases.add(case2);

        testUser.setCaseList(mockCases);
        testCategory.setCaseList(mockCases);

        testCaseDto = new CaseDto();
        testCaseDto.setTitle("Medical Help");
        testCaseDto.setDescription("Need funds for surgery.");
        testCaseDto.setGoal(1000.0);

        testCase = new Case();
        testCase.setId(1);
        testCase.setTitle("Medical Fund");
        testCase.setDescription("help me fund my medication");

        testImage = new File("testImage.jpg");

        // Set up mock security context for authenticated user
        SecurityContext securityContext = mock(SecurityContext.class);
        Authentication authentication = mock(Authentication.class);
        lenient().when(securityContext.getAuthentication()).thenReturn(authentication);
        lenient().when(authentication.getName()).thenReturn("testUser");
        SecurityContextHolder.setContext(securityContext);
    }

    /**
     * Test for retrieving a case by ID.
     * Verifies that the case is returned correctly if it exists.
     */
    @Test
    void getCase_returnCase_caseExists(){
        int caseId = 1;
        when(caseRepository.findById(caseId)).thenReturn(Optional.of(testCase));
        CaseDto caseDto = caseService.getCase(caseId);

        assertNotNull(caseDto);
        assertEquals("Medical Fund", caseDto.getTitle());
        verify(caseRepository, times(1)).findById(caseId);
    }

    /**
     * Test for retrieving a case by ID when it does not exist.
     * Verifies that a {@link CaseNotFoundException} is thrown.
     */
    @Test
    void getCase_throwException_caseDoesNotExist(){
        int caseId = 1;
        when(caseRepository.findById(caseId)).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(CaseNotFoundException.class, () -> caseService.getCase(caseId));
        assertEquals("Case not found with ID: 1", exception.getMessage());
    }

    /**
     * Test for creating a new case with a category name and image.
     * Verifies that the case is saved successfully and that the correct data is set.
     */
    @Test
    void createCase_saveTheCase_givenCaseAndCategoryNameAndImage(){
        when(userRepository.findByUsername("testUser")).thenReturn(Optional.of(testUser));
        when(categoryRepository.findByName("Medical")).thenReturn(testCategory);
        when(caseRepository.save(any(Case.class))).thenAnswer(invocation -> invocation.getArgument(0));

        ImageSavingResponse imageSavingResponse = new ImageSavingResponse();
        imageSavingResponse.setUrl("http://testimage.com/image.jpg");
        imageSavingResponse.setStatus(200);

        CaseServiceImpl caseServiceSpy = spy(caseService);
        doReturn(imageSavingResponse).when(caseServiceSpy).processingSavingImagesToDrive(testImage);

        Case createdCase = caseServiceSpy.createCase(testCaseDto, "Medical", testImage);

        assertNotNull(createdCase);
        assertEquals("Medical Help", createdCase.getTitle());
        assertEquals(CaseStatus.APPROVED, createdCase.getCaseStatus());
        assertEquals(testUser, createdCase.getUser());
        assertEquals(testCategory, createdCase.getCategory());
        verify(caseRepository).save(caseCaptor.capture());
    }

    /**
     * Test for updating an existing case.
     * Verifies that the case is updated with the new details and image path.
     */
    @Test
    void updateCase_updateTheCase_givenCaseIdAndCaseAndImage(){
        Case existingCase = new Case();
        existingCase.setId(1);
        existingCase.setTitle("old title");
        existingCase.setDescription("old description");

        CaseDto updatedCaseDto = new CaseDto();
        updatedCaseDto.setTitle("new title");
        updatedCaseDto.setDescription("new description");

        ImageSavingResponse imageResponse = new ImageSavingResponse();
        imageResponse.setUrl("http://testimage.com/newimage.jpg");
        imageResponse.setStatus(200);

        when(caseRepository.findById(1)).thenReturn(Optional.of(existingCase));
        when(caseRepository.save(any(Case.class))).thenAnswer(invocation -> invocation.getArgument(0));

        CaseServiceImpl caseServiceSpy = spy(caseService);
        doReturn(imageResponse).when(caseServiceSpy).processingSavingImagesToDrive(testImage);

        Case updatedCase = caseServiceSpy.updateCase(1, updatedCaseDto, testImage);

        assertNotNull(updatedCase);
        assertEquals("new title", updatedCase.getTitle());
        assertEquals("new description", updatedCase.getDescription());
        assertEquals("http://testimage.com/newimage.jpg", updatedCase.getImagePath());
        verify(caseRepository).save(caseCaptor.capture());
    }

    /**
     * Test for searching cases based on a query string.
     * Verifies that cases are found when a matching title or description exists.
     */
    @Test
    void searchCases_foundResults_correctQuery(){
        when(caseRepository.findByTitleContainingOrDescriptionContaining("help", "help")).thenReturn(mockCases);
        when(commonService.convertCaseToCaseDTO(any(Case.class))).thenReturn(testCaseDto);

        List<CaseDto> result = caseService.searchCases("help");

        assertNotNull(result);
        assertEquals(2, result.size());
        verify(caseRepository, times(1)).findByTitleContainingOrDescriptionContaining("help", "help");
        verify(commonService, times(2)).convertCaseToCaseDTO(any(Case.class));
    }

    /**
     * Test for searching cases based on a query string when no results are found.
     * Verifies that an empty result list is returned.
     */
    @Test
    void searchCases_noResultsFound_falseQuery(){
        when(caseRepository.findByTitleContainingOrDescriptionContaining("non-existent", "non-existent")).thenReturn(new ArrayList<>());

        List<CaseDto> result = caseService.searchCases("non-existent");

        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(caseRepository, times(1)).findByTitleContainingOrDescriptionContaining("non-existent", "non-existent");
        verify(commonService, times(0)).convertCaseToCaseDTO(any(Case.class));
    }

    /**
     * Test for deleting a case when it exists.
     * Verifies that the case is deleted and the appropriate message is returned.
     */
    @Test
    void deleteCase_caseDeleted_caseExists(){
        int caseId = 1;
        when(caseRepository.existsById(caseId)).thenReturn(true);

        String result = caseService.deleteCase(caseId);
        assertEquals("Case Deleted Successfully", result);
        verify(caseRepository, times(1)).deleteById(caseId);
        verify(caseRepository, times(1)).existsById(caseId);
    }

    /**
     * Test for deleting a case when it does not exist.
     * Verifies that a {@link CaseNotFoundException} is thrown.
     */
    @Test
    void deleteCase_throwException_caseNotFound(){
        int caseId = 9020;
        when(caseRepository.existsById(caseId)).thenReturn(false);

        CaseNotFoundException exception = assertThrows(CaseNotFoundException.class, () -> {
            caseService.deleteCase(caseId);
        });
        assertEquals("Case not found with ID: " + caseId, exception.getMessage());
        verify(caseRepository, times(1)).existsById(caseId);
        verify(caseRepository, never()).deleteById(anyInt());
    }

    /**
     * Test for retrieving all cases.
     * Verifies that all cases are returned when they exist.
     */
    @Test
    void showCases_getsAllTheCases_casesExist(){
        when(caseRepository.findAll()).thenReturn(mockCases);
        when(commonService.convertCaseToCaseDTO(any(Case.class))).thenReturn(testCaseDto);

        List<CaseDto> result = caseService.showCases();

        assertNotNull(result);
        assertEquals(2, result.size());
        verify(caseRepository, times(1)).findAll();
        verify(commonService, times(2)).convertCaseToCaseDTO(any(Case.class));
    }

    /**
     * Test for retrieving cases by username when the user exists.
     * Verifies that the cases of the user are returned correctly.
     */
    @Test
    void getUserCasesByUsername_returnCasesOfUser_validUsername(){
        when(userRepository.findByUsername("testUser")).thenReturn(Optional.of(testUser));
        when(commonService.convertCaseToCaseDTO(any(Case.class))).thenReturn(testCaseDto);

        List<CaseDto> result = caseService.getUserCasesByUsername("testUser");

        assertNotNull(result);
        assertEquals(2, result.size());
        verify(userRepository, times(1)).findByUsername("testUser");
        verify(commonService, times(2)).convertCaseToCaseDTO(any(Case.class));
    }

    /**
     * Test for retrieving cases by username when the user does not exist.
     * Verifies that a {@link UserNotFoundException} is thrown.
     */
    @Test
    void getUserCasesByUsername_throwException_invalidUsername(){
        String username = "non-existent";
        when(userRepository.findByUsername(username)).thenReturn(Optional.empty());

        UserNotFoundException exception = assertThrows(UserNotFoundException.class, () -> {
            caseService.getUserCasesByUsername(username);
        });

        assertEquals("User Not Found With Username: " + username, exception.getMessage());
        verify(userRepository, times(1)).findByUsername(username);
        verify(commonService, never()).convertCaseToCaseDTO(any(Case.class));
    }

    /**
     * Test for retrieving cases by category ID when the category exists.
     * Verifies that the cases of the category are returned correctly.
     */
    @Test
    void getCasesByCategory_returnCases_categoryExists(){
        int categoryId = 1;
        when(categoryRepository.findById(categoryId)).thenReturn(Optional.of(testCategory));
        when(commonService.convertCaseToCaseDTO(any(Case.class))).thenReturn(testCaseDto);

        List<CaseDto> result = caseService.getCasesByCategory(categoryId);

        assertNotNull(result);
        assertEquals(2, result.size());
        verify(categoryRepository, times(1)).findById(categoryId);
        verify(commonService, times(2)).convertCaseToCaseDTO(any(Case.class));
    }

    /**
     * Test for retrieving cases by category ID when the category does not exist.
     * Verifies that a {@link CategoryNotFoundException} is thrown.
     */
    @Test
    void getCasesByCategory_throwsException_categoryNotFound(){
        int categoryId = 900;
        when(categoryRepository.findById(categoryId)).thenReturn(Optional.empty());

        CategoryNotFoundException exception = assertThrows(CategoryNotFoundException.class, () -> {
            caseService.getCasesByCategory(categoryId);
        });

        assertEquals("Category Not Found With Id: " + categoryId, exception.getMessage());
        verify(categoryRepository, times(1)).findById(categoryId);
        verify(commonService, never()).convertCaseToCaseDTO(any(Case.class));
    }
}

