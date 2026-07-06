package com.java.sadna.backend.sportshop.service;

import com.java.sadna.backend.sportshop.config.ImagesProperties;
import com.java.sadna.backend.sportshop.entity.CategoryEntity;
import com.java.sadna.backend.sportshop.exception.BadRequestException;
import com.java.sadna.backend.sportshop.exception.ConflictException;
import com.java.sadna.backend.sportshop.exception.NotFoundException;
import com.java.sadna.backend.sportshop.mapper.entity.dto.CategoryEntityToCategoryDtoMapper;
import com.java.sadna.backend.sportshop.model.CategoryDto;
import com.java.sadna.backend.sportshop.model.enums.ResourceImagePolicy;
import com.java.sadna.backend.sportshop.repository.CategoryRepository;
import com.java.sadna.backend.sportshop.repository.ProductRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.util.Comparator;
import java.util.List;

@Service
@Slf4j
public class CategoryService {

    private static final Comparator<CategoryEntity> LIST_ORDER = Comparator
            .comparing(CategoryEntity::getName, String.CASE_INSENSITIVE_ORDER)
            .thenComparing(CategoryEntity::getId);

    private final CategoryRepository categoryRepository;
    private final ProductRepository productRepository;
    private final CategoryEntityToCategoryDtoMapper categoryEntityToCategoryDtoMapper;
    private final ImagesProperties imagesProperties;

    public CategoryService(CategoryRepository categoryRepository,
                           ProductRepository productRepository,
                           CategoryEntityToCategoryDtoMapper categoryEntityToCategoryDtoMapper,
                           ImagesProperties imagesProperties) {
        this.categoryRepository = categoryRepository;
        this.productRepository = productRepository;
        this.categoryEntityToCategoryDtoMapper = categoryEntityToCategoryDtoMapper;
        this.imagesProperties = imagesProperties;
    }

    @Transactional(readOnly = true)
    public List<CategoryDto> list(Boolean active) {
        return categoryRepository.findAll().stream()
                .filter(c -> active == null || c.isDeleted() != active)
                .sorted(LIST_ORDER)
                .map(categoryEntityToCategoryDtoMapper::map)
                .toList();
    }

    @Transactional
    public CategoryDto createCategory(String name, String iconUrl) {
        String iconFilename = parseIconFilenameOrThrow(iconUrl);
        CategoryEntity saved = categoryRepository.save(new CategoryEntity(name, iconFilename));
        return categoryEntityToCategoryDtoMapper.map(saved);
    }

    @Transactional
    public CategoryDto updateCategory(Long id, String name, String iconUrl, Long actorId) {
        String iconFilename = parseIconFilenameOrThrow(iconUrl);
        int updated = categoryRepository.applyEdit(id, name, iconFilename, actorId, OffsetDateTime.now());
        if (updated == 0) {
            throw new NotFoundException("category.notFound");
        }
        return loadCategoryByIdOrThrow(id);
    }

    @Transactional
    public CategoryDto softDeleteCategory(Long id, Long replacementCategoryId, Long actorId) {
        if (replacementCategoryId.equals(id)) {
            throw new BadRequestException("category.replacementSameAsTarget");
        }
        // X-lock the source up front to drain concurrent product updates and avoid the
        // bulkReassign-X-on-P + applySoftDelete-X-on-C deadlock.
        CategoryEntity source = categoryRepository.findByIdWithWriteLock(id)
                .orElseThrow(() -> new NotFoundException("category.notFound"));
        if (source.isDeleted()) {
            throw new ConflictException("category.alreadyDeleted");
        }
        CategoryEntity replacement = categoryRepository.findByIdWithLock(replacementCategoryId)
                .orElseThrow(() -> new BadRequestException("category.replacementNotActive"));
        if (replacement.isDeleted()) {
            throw new BadRequestException("category.replacementNotActive");
        }
        OffsetDateTime now = OffsetDateTime.now();
        int reassigned = productRepository.bulkReassignByCategoryId(id, replacementCategoryId, actorId, now);
        log.info("Category soft-delete: reassigned {} product(s) from category {} to {}", reassigned, id, replacementCategoryId);
        int updated = categoryRepository.applySoftDelete(id, actorId, now);
        if (updated == 0) {
            throw new ConflictException("category.alreadyDeleted");
        }
        return loadCategoryByIdOrThrow(id);
    }

    @Transactional
    public CategoryDto restoreCategory(Long id, Long actorId) {
        int updated = categoryRepository.applyRestore(id, actorId, OffsetDateTime.now());
        if (updated == 0) {
            throw categoryRepository.existsById(id)
                    ? new ConflictException("category.notDeleted")
                    : new NotFoundException("category.notFound");
        }
        return loadCategoryByIdOrThrow(id);
    }

    public void assertActiveForProductWrite(Long categoryId) {
        CategoryEntity category = categoryRepository.findByIdWithLock(categoryId)
                .orElseThrow(() -> new BadRequestException("category.invalidId"));
        if (category.isDeleted()) {
            throw new ConflictException("category.alreadyDeleted");
        }
    }

    private CategoryDto loadCategoryByIdOrThrow(Long id) {
        CategoryEntity entity = categoryRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("category.notFound"));
        return categoryEntityToCategoryDtoMapper.map(entity);
    }

    private String parseIconFilenameOrThrow(String iconUrl) {
        return imagesProperties.parseFilenameOrBadRequest(
                ResourceImagePolicy.CATEGORIES, iconUrl, "category.invalidIconUrl");
    }
}
