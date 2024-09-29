package com.charity_management_system.service;

import com.charity_management_system.model.Category;

import java.util.List;

public interface CategoryService {

    List<Category> getAllCategories();

    Category getCategoryById(int categoryId);
}
