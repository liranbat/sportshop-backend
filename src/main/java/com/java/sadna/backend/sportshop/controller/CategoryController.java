package com.java.sadna.backend.sportshop.controller;

import com.java.sadna.backend.sportshop.api.generated.categories.api.CategoriesApi;
import com.java.sadna.backend.sportshop.api.generated.categories.model.CategoryDto;
import com.java.sadna.backend.sportshop.mapper.CategoryToCategoryDtoMapper;
import com.java.sadna.backend.sportshop.service.CategoryService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class CategoryController implements CategoriesApi {

    private final CategoryService categoryService;
    private final CategoryToCategoryDtoMapper categoryToCategoryDtoMapper;

    public CategoryController(CategoryService categoryService,
                              CategoryToCategoryDtoMapper categoryToCategoryDtoMapper) {
        this.categoryService = categoryService;
        this.categoryToCategoryDtoMapper = categoryToCategoryDtoMapper;
    }

    @Override
    @PreAuthorize("permitAll()")
    public ResponseEntity<List<CategoryDto>> listActiveCategories() {
        List<CategoryDto> body = categoryService.listActive().stream()
                .map(categoryToCategoryDtoMapper::map)
                .toList();
        return ResponseEntity.ok(body);
    }
}
