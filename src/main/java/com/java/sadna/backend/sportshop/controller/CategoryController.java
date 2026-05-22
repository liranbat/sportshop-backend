package com.java.sadna.backend.sportshop.controller;

import com.java.sadna.backend.sportshop.api.generated.categories.api.CategoriesApi;
import com.java.sadna.backend.sportshop.api.generated.categories.model.Category;
import com.java.sadna.backend.sportshop.mapper.CategoryDtoToCategoryMapper;
import com.java.sadna.backend.sportshop.service.CategoryService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class CategoryController implements CategoriesApi {

    private final CategoryService categoryService;
    private final CategoryDtoToCategoryMapper categoryDtoToCategoryMapper;

    public CategoryController(CategoryService categoryService,
                              CategoryDtoToCategoryMapper categoryDtoToCategoryMapper) {
        this.categoryService = categoryService;
        this.categoryDtoToCategoryMapper = categoryDtoToCategoryMapper;
    }

    @Override
    public ResponseEntity<List<Category>> listCategories(Boolean active) {
        List<Category> body = categoryService.list(active).stream()
                .map(categoryDtoToCategoryMapper::map)
                .toList();
        return ResponseEntity.ok(body);
    }
}
