package com.java.sadna.backend.sportshop.service;

import com.java.sadna.backend.sportshop.entity.CategoryEntity;
import com.java.sadna.backend.sportshop.entity.ProductEntity;
import com.java.sadna.backend.sportshop.mapper.ProductEntityToProductDtoMapper;
import com.java.sadna.backend.sportshop.model.ProductDto;
import com.java.sadna.backend.sportshop.repository.CategoryRepository;
import com.java.sadna.backend.sportshop.repository.ProductRepository;
import com.java.sadna.backend.sportshop.repository.specification.ProductSpecifications;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

@Service
public class ProductService {

    private static final String SORT_FIELD_ID = "id";
    private static final String SORT_FIELD_NAME = "name";
    private static final String SORT_FIELD_PRICE = "price";
    private static final String SORT_FIELD_CATEGORY = "category";
    private static final String SORT_DIRECTION_DESC = "desc";

    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;
    private final ProductEntityToProductDtoMapper productEntityToProductDtoMapper;

    public ProductService(ProductRepository productRepository,
                          CategoryRepository categoryRepository,
                          ProductEntityToProductDtoMapper productEntityToProductDtoMapper) {
        this.productRepository = productRepository;
        this.categoryRepository = categoryRepository;
        this.productEntityToProductDtoMapper = productEntityToProductDtoMapper;
    }

    @Transactional(readOnly = true)
    public List<ProductDto> list(Boolean active,
                                 String search,
                                 List<Long> categoryIds,
                                 BigDecimal priceMin,
                                 BigDecimal priceMax,
                                 String sortField,
                                 String sortDirection) {
        Specification<ProductEntity> spec = Specification.allOf(
                ProductSpecifications.active(active),
                ProductSpecifications.nameContainsIgnoreCase(search),
                ProductSpecifications.categoryIdIn(categoryIds),
                ProductSpecifications.priceGte(priceMin),
                ProductSpecifications.priceLte(priceMax)
        );

        Sort sort = buildSort(sortField, sortDirection);

        List<ProductDto> products = productRepository.findAll(spec, sort).stream()
                .map(productEntityToProductDtoMapper::map)
                .toList();

        if (SORT_FIELD_CATEGORY.equalsIgnoreCase(sortField)) {
            return sortByCategoryName(products, sortDirection);
        }
        return products;
    }

    private Sort buildSort(String sortField, String sortDirection) {
        if (sortField == null || sortField.isBlank()) {
            return Sort.by(SORT_FIELD_ID).ascending();
        }
        if (SORT_FIELD_CATEGORY.equalsIgnoreCase(sortField)) {
            return Sort.unsorted();
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
        return Sort.by(SORT_FIELD_ID).ascending();
    }

    private List<ProductDto> sortByCategoryName(List<ProductDto> products, String sortDirection) {
        Map<Long, String> categoryNamesById = categoryRepository.findAll().stream()
                .collect(java.util.stream.Collectors.toMap(CategoryEntity::getId, CategoryEntity::getName));
        Comparator<ProductDto> byCategoryName = Comparator.comparing(
                product -> categoryNamesById.getOrDefault(product.getCategoryId(), ""),
                String.CASE_INSENSITIVE_ORDER
        );
        if (SORT_DIRECTION_DESC.equalsIgnoreCase(sortDirection)) {
            byCategoryName = byCategoryName.reversed();
        }
        return products.stream().sorted(byCategoryName).toList();
    }
}
