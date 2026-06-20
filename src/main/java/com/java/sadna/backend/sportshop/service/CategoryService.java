package com.java.sadna.backend.sportshop.service;

import com.java.sadna.backend.sportshop.config.AppProperties;
import com.java.sadna.backend.sportshop.config.ImagesProperties;
import com.java.sadna.backend.sportshop.entity.CategoryEntity;
import com.java.sadna.backend.sportshop.exception.BadRequestException;
import com.java.sadna.backend.sportshop.exception.ConflictException;
import com.java.sadna.backend.sportshop.exception.NotFoundException;
import com.java.sadna.backend.sportshop.mapper.CategoryEntityToCategoryDtoMapper;
import com.java.sadna.backend.sportshop.model.CategoryDto;
import com.java.sadna.backend.sportshop.model.enums.ResourceImagePolicy;
import com.java.sadna.backend.sportshop.repository.CategoryRepository;
import com.java.sadna.backend.sportshop.repository.ProductRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.util.Comparator;
import java.util.List;

@Service
public class CategoryService {

    private static final Logger log = LoggerFactory.getLogger(CategoryService.class);

    private static final String CATEGORY_NOT_FOUND_MESSAGE = "Category not found.";
    private static final String CATEGORY_ALREADY_DELETED_MESSAGE =
            "This category is already deleted — another admin already deleted it.";
    private static final String CATEGORY_NOT_DELETED_MESSAGE =
            "This category is no longer deleted — another admin already restored it.";
    private static final String REPLACEMENT_SAME_AS_TARGET_MESSAGE =
            "Replacement category cannot be the category being deleted.";
    private static final String REPLACEMENT_NOT_ACTIVE_MESSAGE =
            "Replacement category does not exist or is itself soft-deleted.";
    private static final String INVALID_ICON_URL_MESSAGE =
            "Icon URL is not a valid category icon URL produced by the upload pipeline.";

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
                           AppProperties appProperties) {
        this.categoryRepository = categoryRepository;
        this.productRepository = productRepository;
        this.categoryEntityToCategoryDtoMapper = categoryEntityToCategoryDtoMapper;
        this.imagesProperties = appProperties.getImages();
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
    public CategoryDto createCategory(String name, String iconUrl, Long actorId) {
        String iconFilename = parseIconFilenameOrThrow(iconUrl);
        CategoryEntity saved = categoryRepository.save(new CategoryEntity(name, iconFilename));
        return categoryEntityToCategoryDtoMapper.map(saved);
    }

    @Transactional
    public CategoryDto updateCategory(Long id, String name, String iconUrl, Long actorId) {
        String iconFilename = parseIconFilenameOrThrow(iconUrl);
        int updated = categoryRepository.applyEdit(id, name, iconFilename, actorId, OffsetDateTime.now());
        if (updated == 0) {
            throw new NotFoundException(CATEGORY_NOT_FOUND_MESSAGE);
        }
        return loadCategoryByIdOrThrow(id);
    }

    @Transactional
    public CategoryDto softDeleteCategory(Long id, Long replacementCategoryId, Long actorId) {
        if (replacementCategoryId.equals(id)) {
            throw new BadRequestException(REPLACEMENT_SAME_AS_TARGET_MESSAGE);
        }
        CategoryEntity source = categoryRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(CATEGORY_NOT_FOUND_MESSAGE));
        if (source.isDeleted()) {
            throw new ConflictException(CATEGORY_ALREADY_DELETED_MESSAGE);
        }
        CategoryEntity replacement = categoryRepository.findByIdForShare(replacementCategoryId)
                .orElseThrow(() -> new BadRequestException(REPLACEMENT_NOT_ACTIVE_MESSAGE));
        if (replacement.isDeleted()) {
            throw new BadRequestException(REPLACEMENT_NOT_ACTIVE_MESSAGE);
        }
        OffsetDateTime now = OffsetDateTime.now();
        int reassigned = productRepository.bulkReassignByCategoryId(id, replacementCategoryId, actorId, now);
        log.info("Category soft-delete: reassigned {} product(s) from category {} to {}", reassigned, id, replacementCategoryId);
        int updated = categoryRepository.applySoftDelete(id, actorId, now);
        if (updated == 0) {
            throw new ConflictException(CATEGORY_ALREADY_DELETED_MESSAGE);
        }
        return loadCategoryByIdOrThrow(id);
    }

    @Transactional
    public CategoryDto restoreCategory(Long id, Long actorId) {
        int updated = categoryRepository.applyRestore(id, actorId, OffsetDateTime.now());
        if (updated == 0) {
            throw categoryRepository.existsById(id)
                    ? new ConflictException(CATEGORY_NOT_DELETED_MESSAGE)
                    : new NotFoundException(CATEGORY_NOT_FOUND_MESSAGE);
        }
        return loadCategoryByIdOrThrow(id);
    }

    private CategoryDto loadCategoryByIdOrThrow(Long id) {
        CategoryEntity entity = categoryRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(CATEGORY_NOT_FOUND_MESSAGE));
        return categoryEntityToCategoryDtoMapper.map(entity);
    }

    private String parseIconFilenameOrThrow(String iconUrl) {
        try {
            return imagesProperties.parseFilename(ResourceImagePolicy.CATEGORIES, iconUrl);
        } catch (IllegalArgumentException e) {
            throw new BadRequestException(INVALID_ICON_URL_MESSAGE);
        }
    }
}
