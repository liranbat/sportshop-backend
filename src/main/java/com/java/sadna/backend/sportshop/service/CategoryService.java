package com.java.sadna.backend.sportshop.service;

import com.java.sadna.backend.sportshop.entity.CategoryEntity;
import com.java.sadna.backend.sportshop.mapper.CategoryEntityToCategoryDtoMapper;
import com.java.sadna.backend.sportshop.model.CategoryDto;
import com.java.sadna.backend.sportshop.repository.CategoryRepository;
import com.java.sadna.backend.sportshop.repository.specification.CategorySpecifications;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class CategoryService {

    private static final Sort BY_NAME_ASC = Sort.by("name").ascending();

    private final CategoryRepository categoryRepository;
    private final CategoryEntityToCategoryDtoMapper categoryEntityToCategoryDtoMapper;

    public CategoryService(CategoryRepository categoryRepository,
                           CategoryEntityToCategoryDtoMapper categoryEntityToCategoryDtoMapper) {
        this.categoryRepository = categoryRepository;
        this.categoryEntityToCategoryDtoMapper = categoryEntityToCategoryDtoMapper;
    }

    @Transactional(readOnly = true)
    public List<CategoryDto> list(Boolean active) {
        Specification<CategoryEntity> spec = Specification.allOf(
                CategorySpecifications.active(active)
        );
        return categoryRepository.findAll(spec, BY_NAME_ASC).stream()
                .map(categoryEntityToCategoryDtoMapper::map)
                .toList();
    }
}
