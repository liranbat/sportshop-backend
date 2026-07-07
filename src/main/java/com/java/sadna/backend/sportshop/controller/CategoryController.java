package com.java.sadna.backend.sportshop.controller;

import com.java.sadna.backend.sportshop.api.generated.categories.api.AdminCategoriesApi;
import com.java.sadna.backend.sportshop.api.generated.categories.api.CategoriesApi;
import com.java.sadna.backend.sportshop.api.generated.categories.model.Category;
import com.java.sadna.backend.sportshop.api.generated.categories.model.CategorySoftDeleteRequest;
import com.java.sadna.backend.sportshop.api.generated.categories.model.CategoryWriteRequest;
import com.java.sadna.backend.sportshop.mapper.dto.response.CategoryDtoToCategoryMapper;
import com.java.sadna.backend.sportshop.security.AuthorityRules;
import com.java.sadna.backend.sportshop.security.SecurityContextUtils;
import com.java.sadna.backend.sportshop.service.CategoryService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class CategoryController implements CategoriesApi, AdminCategoriesApi {

    private final CategoryService categoryService;
    private final CategoryDtoToCategoryMapper categoryDtoToCategoryMapper;

    public CategoryController(CategoryService categoryService,
                              CategoryDtoToCategoryMapper categoryDtoToCategoryMapper) {
        this.categoryService = categoryService;
        this.categoryDtoToCategoryMapper = categoryDtoToCategoryMapper;
    }

    @Override
    public ResponseEntity<List<Category>> listCategories() {
        List<Category> body = categoryService.list(true).stream()
                .map(categoryDtoToCategoryMapper::map)
                .toList();
        return ResponseEntity.ok(body);
    }

    @Override
    @PreAuthorize(AuthorityRules.ADMIN)
    public ResponseEntity<List<Category>> listAdminCategories() {
        List<Category> body = categoryService.list(null).stream()
                .map(categoryDtoToCategoryMapper::map)
                .toList();
        return ResponseEntity.ok(body);
    }

    @Override
    @PreAuthorize(AuthorityRules.ADMIN)
    public ResponseEntity<Category> createAdminCategory(CategoryWriteRequest req) {
        return ResponseEntity.ok(
                categoryDtoToCategoryMapper.map(
                        categoryService.createCategory(req.getName(), req.getIcon())
                )
        );
    }

    @Override
    @PreAuthorize(AuthorityRules.ADMIN)
    public ResponseEntity<Category> updateAdminCategory(Long id, CategoryWriteRequest req) {
        Long actorId = SecurityContextUtils.currentUserIdOrThrow();
        return ResponseEntity.ok(
                categoryDtoToCategoryMapper.map(
                        categoryService.updateCategory(id, req.getName(), req.getIcon(), actorId)
                )
        );
    }

    @Override
    @PreAuthorize(AuthorityRules.ADMIN)
    public ResponseEntity<Category> softDeleteAdminCategory(Long id, CategorySoftDeleteRequest req) {
        Long actorId = SecurityContextUtils.currentUserIdOrThrow();
        return ResponseEntity.ok(
                categoryDtoToCategoryMapper.map(
                        categoryService.softDeleteCategory(id, req.getReplacementCategoryId(), actorId)
                )
        );
    }

    @Override
    @PreAuthorize(AuthorityRules.ADMIN)
    public ResponseEntity<Category> restoreAdminCategory(Long id) {
        Long actorId = SecurityContextUtils.currentUserIdOrThrow();
        return ResponseEntity.ok(
                categoryDtoToCategoryMapper.map(categoryService.restoreCategory(id, actorId))
        );
    }
}
