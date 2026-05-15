package com.java.sadna.backend.sportshop.service;

import com.java.sadna.backend.sportshop.mapper.CategoryEntityToCategoryMapper;
import com.java.sadna.backend.sportshop.model.Category;
import com.java.sadna.backend.sportshop.repository.CategoryRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class CategoryService {

    private final CategoryRepository categoryRepository;
    private final CategoryEntityToCategoryMapper categoryEntityToCategoryMapper;

    public CategoryService(CategoryRepository categoryRepository,
                           CategoryEntityToCategoryMapper categoryEntityToCategoryMapper) {
        this.categoryRepository = categoryRepository;
        this.categoryEntityToCategoryMapper = categoryEntityToCategoryMapper;
    }

    @Transactional(readOnly = true)
    public List<Category> listActive() {
        return categoryRepository.findByDeletedFalseOrderByNameAsc().stream()
                .map(categoryEntityToCategoryMapper::map)
                .toList();
    }
}
