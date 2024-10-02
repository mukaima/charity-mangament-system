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
@RequestMapping("api/v1/categories")
@RequiredArgsConstructor
public class CategoryController {

    private final CategoryService categoryService;

    /**
     * gets all the categories we have
     * @return list of all categories
     * */
    @GetMapping("/getAll")
    public ResponseEntity<List<Category>> getAll(){
        return ResponseEntity.ok(categoryService.getAllCategories());
    }

    /**
     * get a specific category by its id
     * @param categoryId the id of the category
     * @return the category object with that specified id
     */
    @GetMapping("/getById")
    public ResponseEntity<Category> getCategoryById(@RequestParam int categoryId){
        return ResponseEntity.ok(categoryService.getCategoryById(categoryId));
    }
}
