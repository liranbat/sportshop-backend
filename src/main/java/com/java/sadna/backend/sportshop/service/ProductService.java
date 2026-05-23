package com.java.sadna.backend.sportshop.service;

import com.java.sadna.backend.sportshop.entity.CategoryEntity;
import com.java.sadna.backend.sportshop.entity.ProductEntity;
import com.java.sadna.backend.sportshop.exception.NotFoundException;
import com.java.sadna.backend.sportshop.mapper.ProductEntityToProductDtoMapper;
import com.java.sadna.backend.sportshop.mapper.ProductStockEntityToProductSizeDtoMapper;
import com.java.sadna.backend.sportshop.model.PagedResult;
import com.java.sadna.backend.sportshop.model.ProductDetailDto;
import com.java.sadna.backend.sportshop.model.ProductDto;
import com.java.sadna.backend.sportshop.model.ProductSizeDto;
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

@Service
public class ProductService {

    private static final String SORT_FIELD_ID = "id";
    private static final String SORT_FIELD_NAME = "name";
    private static final String SORT_FIELD_PRICE = "price";
    private static final String SORT_FIELD_CATEGORY = "category";
    // JPA path resolved via the read-only ManyToOne CategoryEntity association on ProductEntity.
    private static final String SORT_PATH_CATEGORY_NAME = "category.name";
    private static final String SORT_DIRECTION_DESC = "desc";

    private static final int DEFAULT_PAGE_SIZE = 9;

    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;
    private final ProductStockRepository productStockRepository;
    private final ProductEntityToProductDtoMapper productEntityToProductDtoMapper;
    private final ProductStockEntityToProductSizeDtoMapper productStockEntityToProductSizeDtoMapper;
    private final PaginationService paginationService;

    public ProductService(ProductRepository productRepository,
                          CategoryRepository categoryRepository,
                          ProductStockRepository productStockRepository,
                          ProductEntityToProductDtoMapper productEntityToProductDtoMapper,
                          ProductStockEntityToProductSizeDtoMapper productStockEntityToProductSizeDtoMapper,
                          PaginationService paginationService) {
        this.productRepository = productRepository;
        this.categoryRepository = categoryRepository;
        this.productStockRepository = productStockRepository;
        this.productEntityToProductDtoMapper = productEntityToProductDtoMapper;
        this.productStockEntityToProductSizeDtoMapper = productStockEntityToProductSizeDtoMapper;
        this.paginationService = paginationService;
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

    private Sort buildSort(String sortField, String sortDirection) {
        if (sortField == null || sortField.isBlank()) {
            return Sort.by(SORT_FIELD_ID).ascending();
        }
        Sort.Direction direction = SORT_DIRECTION_DESC.equalsIgnoreCase(sortDirection)
                ? Sort.Direction.DESC
                : Sort.Direction.ASC;
        if (SORT_FIELD_PRICE.equalsIgnoreCase(sortField)) {
            return Sort.by(direction, SORT_FIELD_PRICE);
        }
        if (SORT_FIELD_NAME.equalsIgnoreCase(sortField)) {
            return Sort.by(direction, SORT_FIELD_NAME);
        }
        if (SORT_FIELD_CATEGORY.equalsIgnoreCase(sortField)) {
            return Sort.by(direction, SORT_PATH_CATEGORY_NAME);
        }
        return Sort.by(SORT_FIELD_ID).ascending();
    }
}
