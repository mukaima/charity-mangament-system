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

    @Override
    public List<Category> getAllCategories() {
        return categoryRepository.findAll();
    }

    @Override
    public Category getCategoryById(int categoryId) {
        return categoryRepository.findById(categoryId).orElseThrow(() -> new RuntimeException("category not found"));
    }
}
