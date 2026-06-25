package com.java.sadna.backend.sportshop.service;

import com.java.sadna.backend.sportshop.config.AppProperties;
import com.java.sadna.backend.sportshop.config.ImagesProperties;
import com.java.sadna.backend.sportshop.entity.CategoryEntity;
import com.java.sadna.backend.sportshop.entity.ProductEntity;
import com.java.sadna.backend.sportshop.entity.ProductStockEntity;
import com.java.sadna.backend.sportshop.exception.BadRequestException;
import com.java.sadna.backend.sportshop.exception.NotFoundException;
import com.java.sadna.backend.sportshop.mapper.ProductEntityToProductDtoMapper;
import com.java.sadna.backend.sportshop.mapper.ProductStockEntityToProductSizeDtoMapper;
import com.java.sadna.backend.sportshop.model.PagedResult;
import com.java.sadna.backend.sportshop.model.ProductCreateRequestDto;
import com.java.sadna.backend.sportshop.model.ProductDetailDto;
import com.java.sadna.backend.sportshop.model.ProductDto;
import com.java.sadna.backend.sportshop.model.ProductSizeDto;
import com.java.sadna.backend.sportshop.model.ProductStockInputDto;
import com.java.sadna.backend.sportshop.model.enums.ResourceImagePolicy;
import com.java.sadna.backend.sportshop.repository.CategoryRepository;
import com.java.sadna.backend.sportshop.repository.ProductRepository;
import com.java.sadna.backend.sportshop.repository.ProductStockRepository;
import com.java.sadna.backend.sportshop.repository.specification.ProductSpecifications;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@Service
public class ProductService {

    private static final String SORT_FIELD_ID = "id";
    private static final String SORT_FIELD_NAME = "name";
    private static final String SORT_FIELD_PRICE = "price";
    private static final String SORT_FIELD_CATEGORY = "category";
    private static final String SORT_FIELD_UPDATED_AT = "updatedAt";
    // JPA path resolved via the read-only ManyToOne CategoryEntity association on ProductEntity.
    private static final String SORT_PATH_CATEGORY_NAME = "category.name";
    private static final String SORT_DIRECTION_DESC = "desc";

    private static final int DEFAULT_PAGE_SIZE = 9;
    private static final String ONE_SIZE_TOKEN = "ONE_SIZE";
    private static final int SIZE_TOKEN_MAX_LENGTH = 20;

    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;
    private final ProductStockRepository productStockRepository;
    private final CategoryService categoryService;
    private final ProductEntityToProductDtoMapper productEntityToProductDtoMapper;
    private final ProductStockEntityToProductSizeDtoMapper productStockEntityToProductSizeDtoMapper;
    private final PaginationService paginationService;
    private final ImagesProperties imagesProperties;

    public ProductService(ProductRepository productRepository,
                          CategoryRepository categoryRepository,
                          ProductStockRepository productStockRepository,
                          CategoryService categoryService,
                          ProductEntityToProductDtoMapper productEntityToProductDtoMapper,
                          ProductStockEntityToProductSizeDtoMapper productStockEntityToProductSizeDtoMapper,
                          PaginationService paginationService,
                          AppProperties appProperties) {
        this.productRepository = productRepository;
        this.categoryRepository = categoryRepository;
        this.productStockRepository = productStockRepository;
        this.categoryService = categoryService;
        this.productEntityToProductDtoMapper = productEntityToProductDtoMapper;
        this.productStockEntityToProductSizeDtoMapper = productStockEntityToProductSizeDtoMapper;
        this.paginationService = paginationService;
        this.imagesProperties = appProperties.getImages();
    }

    @Transactional(readOnly = true)
    public PagedResult<ProductDto> list(Boolean active,
                                        String search,
                                        List<Long> categoryIds,
                                        BigDecimal priceMin,
                                        BigDecimal priceMax,
                                        String sortField,
                                        String sortDirection,
                                        Integer page,
                                        Integer pageSize) {
        Specification<ProductEntity> spec = Specification.allOf(
                ProductSpecifications.active(active),
                ProductSpecifications.nameContainsIgnoreCase(search),
                ProductSpecifications.categoryIdIn(categoryIds),
                ProductSpecifications.priceGte(priceMin),
                ProductSpecifications.priceLte(priceMax)
        );

        Sort sort = buildSort(sortField, sortDirection);
        return paginationService.paginate(
                productRepository, spec, sort, page, pageSize, DEFAULT_PAGE_SIZE,
                productEntityToProductDtoMapper
        );
    }

    @Transactional
    public ProductDetailDto create(ProductCreateRequestDto input) {
        validateStockBySize(input.isMultiSize(), input.getStockBySize());
        categoryService.assertActiveForProductWrite(input.getCategoryId());
        String imageFilename = parseProductImageFilenameOrThrow(input.getImageUrl());

        ProductEntity saved = productRepository.saveAndFlush(
                new ProductEntity(
                        input.getName(),
                        input.getDescription(),
                        input.getCategoryId(),
                        input.isMultiSize(),
                        imageFilename,
                        input.getPrice())
        );
        Long productId = saved.getId();

        List<ProductStockEntity> stockRows = input.getStockBySize().entrySet().stream()
                .map(entry -> new ProductStockEntity(
                        productId,
                        entry.getKey(),
                        entry.getValue().getQuantity(),
                        entry.getValue().getLowStockThreshold()))
                .toList();
        productStockRepository.saveAll(stockRows);

        return getById(productId);
    }

    @Transactional(readOnly = true)
    public ProductDetailDto getById(Long id) {
        // Archived products are hidden from the Product Details page — same surface as
        // a missing row, so we collapse both to a single 404 via NotFoundException.
        ProductEntity productEntity = productRepository.findById(id)
                .filter(p -> !p.isArchived())
                .orElseThrow(() -> new NotFoundException("Product " + id + " not found."));

        String categoryName = categoryRepository.findById(productEntity.getCategoryId())
                .map(CategoryEntity::getName)
                .orElse(null);

        List<ProductSizeDto> sizes = productStockRepository
                .findByProductId(id)
                .stream()
                .map(productStockEntityToProductSizeDtoMapper::map)
                .toList();

        ProductDto product = productEntityToProductDtoMapper.map(productEntity);
        return new ProductDetailDto(product, categoryName, sizes);
    }

    private static void validateStockBySize(boolean isMultiSize, Map<String, ProductStockInputDto> stockBySize) {
        if (stockBySize == null || stockBySize.isEmpty()) {
            throw new BadRequestException("stockBySize must contain at least one entry.");
        }
        stockBySize.forEach((sizeToken, stock) -> {
            if (sizeToken == null || sizeToken.isBlank() || sizeToken.length() > SIZE_TOKEN_MAX_LENGTH) {
                throw new BadRequestException(
                        "Each stockBySize key must be a non-blank size token of length 1.."
                                + SIZE_TOKEN_MAX_LENGTH + ".");
            }
            if (stock == null) {
                throw new BadRequestException("stockBySize entry for size '" + sizeToken + "' is missing.");
            }
            if (stock.getQuantity() < 0) {
                throw new BadRequestException("Quantity for size '" + sizeToken + "' must be >= 0.");
            }
            if (stock.getLowStockThreshold() != null && stock.getLowStockThreshold() < 0) {
                throw new BadRequestException(
                        "lowStockThreshold for size '" + sizeToken + "' must be >= 0.");
            }
        });
        if (isMultiSize) {
            if (stockBySize.containsKey(ONE_SIZE_TOKEN)) {
                throw new BadRequestException(
                        "Multi-size products must not use the '" + ONE_SIZE_TOKEN + "' size token.");
            }
        } else if (stockBySize.size() != 1 || !stockBySize.containsKey(ONE_SIZE_TOKEN)) {
            throw new BadRequestException(
                    "Single-size products must have exactly one stockBySize entry keyed '"
                            + ONE_SIZE_TOKEN + "'.");
        }
    }

    private String parseProductImageFilenameOrThrow(String imageUrl) {
        try {
            return imagesProperties.parseFilename(ResourceImagePolicy.PRODUCTS, imageUrl);
        } catch (IllegalArgumentException e) {
            throw new BadRequestException("Invalid imageUrl.");
        }
    }

    private Sort buildSort(String sortField, String sortDirection) {
        Sort.Order idTieBreak = Sort.Order.asc(SORT_FIELD_ID);
        if (sortField == null || sortField.isBlank()) {
            return Sort.by(idTieBreak);
        }
        Sort.Direction direction = SORT_DIRECTION_DESC.equalsIgnoreCase(sortDirection)
                ? Sort.Direction.DESC
                : Sort.Direction.ASC;
        String primaryField = resolvePrimarySortField(sortField);
        if (primaryField == null) {
            return Sort.by(idTieBreak);
        }
        if (SORT_FIELD_ID.equalsIgnoreCase(primaryField)) {
            return Sort.by(new Sort.Order(direction, SORT_FIELD_ID));
        }
        return Sort.by(new Sort.Order(direction, primaryField), idTieBreak);
    }

    private String resolvePrimarySortField(String sortField) {
        if (SORT_FIELD_PRICE.equalsIgnoreCase(sortField)) return SORT_FIELD_PRICE;
        if (SORT_FIELD_NAME.equalsIgnoreCase(sortField)) return SORT_FIELD_NAME;
        if (SORT_FIELD_CATEGORY.equalsIgnoreCase(sortField)) return SORT_PATH_CATEGORY_NAME;
        if (SORT_FIELD_UPDATED_AT.equalsIgnoreCase(sortField)) return SORT_FIELD_UPDATED_AT;
        return null;
    }
}
