package com.charity_management_system.service.impl;

import com.charity_management_system.model.Category;
import com.charity_management_system.repository.CategoryRepository;
import com.charity_management_system.service.CategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CategoryServiceImpl implements CategoryService {

    private final CategoryRepository categoryRepository;


    /**
     * Retrieves all categories.
     *
     * @return A list of all Category entities.
     */
    @Override
    public List<Category> getAllCategories() {
        return categoryRepository.findAll();
    }

    /**
     * Retrieves a category by its ID.
     *
     * @param categoryId The ID of the category to retrieve.
     * @return The Category entity.
     */
    @Override
    public Category getCategoryById(int categoryId) {
        return categoryRepository.findById(categoryId).orElseThrow(() -> new RuntimeException("category not found"));
    }
}
