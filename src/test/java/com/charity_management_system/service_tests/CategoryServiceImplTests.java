package com.charity_management_system.service_tests;

import com.charity_management_system.exception.custom.CategoryNotFoundException;
import com.charity_management_system.model.Category;
import com.charity_management_system.repository.CategoryRepository;
import com.charity_management_system.service.impl.CategoryServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Unit tests for the {@link CategoryServiceImpl} class using Mockito.
 * This test class verifies the behavior of the category service methods, such as retrieving all categories
 * and retrieving a category by its ID.
 *
 * <p>Mocks are used to simulate the behavior of dependent classes, specifically the {@link CategoryRepository}.</p>
 *
 * <p>This class uses the {@link MockitoExtension} to enable mock injection and simplify testing.</p>
 *
 * <p>Each test method is designed to cover specific scenarios, including both successful and failed operations.</p>
 */
@ExtendWith(MockitoExtension.class)
class CategoryServiceImplTests {

    /**
     * Mocked {@link CategoryRepository} used to simulate category data persistence.
     */
    @Mock
    private CategoryRepository categoryRepository;

    /**
     * The {@link CategoryServiceImpl} instance under test, with dependencies injected via {@link InjectMocks}.
     */
    @InjectMocks
    private CategoryServiceImpl categoryService;

    /**
     * Sample {@link Category} object used in test cases.
     */
    private Category testCategory;

    /**
     * List of mock {@link Category} objects used in test cases.
     */
    private List<Category> mockCategories;

    /**
     * Setup method executed before each test.
     * Initializes sample category data and a list of mock categories for testing.
     */
    @BeforeEach
    void setup() {
        // Setup test category data
        testCategory = new Category();
        testCategory.setId(1);
        testCategory.setName("Education");

        // Initialize a list of mock categories
        mockCategories = Arrays.asList(
                new Category(1, "Education", new ArrayList<>()),
                new Category(2, "Medical", new ArrayList<>())
        );
    }

    /**
     * Test for retrieving all categories.
     * Verifies that a list of categories is returned when categories exist.
     */
    @Test
    void getAllCategories_returnListOfCategories_categoriesExist() {
        when(categoryRepository.findAll()).thenReturn(mockCategories);

        List<Category> result = categoryService.getAllCategories();

        assertNotNull(result);
        assertEquals(2, result.size());
        verify(categoryRepository, times(1)).findAll();
    }

    /**
     * Test for retrieving a category by its ID when the category exists.
     * Verifies that the correct category is returned for the provided valid ID.
     */
    @Test
    void getCategoryById_returnCategory_validId() {
        when(categoryRepository.findById(1)).thenReturn(Optional.of(testCategory));

        Category result = categoryService.getCategoryById(1);

        assertNotNull(result);
        assertEquals("Education", result.getName());
        verify(categoryRepository, times(1)).findById(1);
    }

    /**
     * Test for retrieving a category by its ID when the category does not exist.
     * Verifies that a {@link CategoryNotFoundException} is thrown for an invalid ID.
     */
    @Test
    void getCategoryById_throwException_invalidId() {
        int categoryId = 1;
        when(categoryRepository.findById(categoryId)).thenReturn(Optional.empty());

        CategoryNotFoundException exception = assertThrows(CategoryNotFoundException.class, () -> {
            categoryService.getCategoryById(categoryId);
        });

        assertEquals("Category Not Found With Id: " + categoryId, exception.getMessage());
        verify(categoryRepository, times(1)).findById(categoryId);
    }
}
