package com.charity_management_system.controller;

import com.charity_management_system.model.Category;
import com.charity_management_system.service.CategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("api/categories")
@RequiredArgsConstructor
public class CategoryController {

    private final CategoryService categoryService;

    @GetMapping("/getAll")
    public ResponseEntity<List<Category>> getAll(){
        return ResponseEntity.ok(categoryService.getAllCategories());
    }

    @GetMapping("/getById")
    public ResponseEntity<Category> getCategoryById(@RequestParam int categoryId){
        return ResponseEntity.ok(categoryService.getCategoryById(categoryId));
    }
}
