package com.java.sadna.backend.sportshop.service;

import com.java.sadna.backend.sportshop.api.generated.stock.model.StockArchiveStatusFilter;
import com.java.sadna.backend.sportshop.api.generated.stock.model.StockStatusFilter;
import com.java.sadna.backend.sportshop.entity.ProductEntity;
import com.java.sadna.backend.sportshop.entity.ProductStockEntity;
import com.java.sadna.backend.sportshop.entity.id.ProductStockId;
import com.java.sadna.backend.sportshop.exception.BadRequestException;
import com.java.sadna.backend.sportshop.exception.ConflictException;
import com.java.sadna.backend.sportshop.exception.NotFoundException;
import com.java.sadna.backend.sportshop.mapper.ProductStockEntityToStockRowDtoMapper;
import com.java.sadna.backend.sportshop.model.PagedResult;
import com.java.sadna.backend.sportshop.model.StockRowDto;
import com.java.sadna.backend.sportshop.repository.ProductRepository;
import com.java.sadna.backend.sportshop.repository.ProductStockRepository;
import com.java.sadna.backend.sportshop.repository.specification.StockSpecifications;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class StockService {

    static final String ONE_SIZE_TOKEN = "ONE_SIZE";

    private static final int DEFAULT_PAGE_SIZE = 50;
    private static final String SORT_FIELD_NAME = "name";
    private static final String SORT_FIELD_QUANTITY = "quantity";
    private static final String SORT_FIELD_THRESHOLD = "threshold";
    // JPA paths -- "product.name" resolves via the read-only ManyToOne ProductEntity association.
    private static final String SORT_PATH_PRODUCT_NAME = "product.name";
    private static final String SORT_PATH_QUANTITY = "quantity";
    private static final String SORT_PATH_THRESHOLD = "lowStockThreshold";
    // Sort tiebreak paths -- size first, then productId, for stable pagination.
    private static final String SORT_TIEBREAK_PATH_SIZE = "size";
    private static final String SORT_TIEBREAK_PATH_PRODUCT_ID = "productId";
    private static final String SORT_DIRECTION_DESC = "desc";

    private final ProductStockRepository productStockRepository;
    private final ProductRepository productRepository;
    private final PaginationService paginationService;
    private final ProductStockEntityToStockRowDtoMapper productStockEntityToStockRowDtoMapper;

    public StockService(ProductStockRepository productStockRepository,
                        ProductRepository productRepository,
                        PaginationService paginationService,
                        ProductStockEntityToStockRowDtoMapper productStockEntityToStockRowDtoMapper) {
        this.productStockRepository = productStockRepository;
        this.productRepository = productRepository;
        this.paginationService = paginationService;
        this.productStockEntityToStockRowDtoMapper = productStockEntityToStockRowDtoMapper;
    }

    @Transactional(readOnly = true)
    public PagedResult<StockRowDto> list(String searchName,
                                         List<String> sizes,
                                         StockStatusFilter stockStatus,
                                         StockArchiveStatusFilter archiveStatus,
                                         String sortField,
                                         String sortDirection,
                                         Integer page,
                                         Integer pageSize) {
        Specification<ProductStockEntity> spec = Specification.allOf(
                StockSpecifications.productNameContainsIgnoreCase(searchName),
                StockSpecifications.sizeIn(sizes),
                StockSpecifications.stockStatus(stockStatus == null ? null : stockStatus.getValue()),
                StockSpecifications.archiveStatus(archiveStatus == null ? null : archiveStatus.getValue())
        );
        Sort sort = buildSort(sortField, sortDirection);
        return paginationService.paginate(
                productStockRepository, spec, sort, page, pageSize, DEFAULT_PAGE_SIZE,
                productStockEntityToStockRowDtoMapper
        );
    }

    @Transactional
    public StockRowDto setStock(Long productId, String size, int quantity, Integer threshold) {
        int affected = productStockRepository.adminSet(productId, size, quantity, threshold);
        if (affected == 0) {
            throw stockRowNotFound(productId, size);
        }
        return readRowOrThrow(productId, size);
    }

    @Transactional
    public StockRowDto adjustQuantity(Long productId, String size, int delta) {
        if (delta == 0) {
            throw new BadRequestException("delta must be non-zero.");
        }
        int affected = productStockRepository.adminAdjust(productId, size, delta);
        if (affected == 0) {
            throw new ConflictException(
                    "Cannot adjust quantity by " + delta
                            + ": stock row no longer exists or quantity would go below 0.");
        }
        return readRowOrThrow(productId, size);
    }

    @Transactional
    public StockRowDto addSize(Long productId, String size, int quantity, Integer threshold) {
        String trimmed = size == null ? "" : size.trim();
        if (trimmed.isEmpty()) {
            throw new BadRequestException("size must be non-blank.");
        }
        if (ONE_SIZE_TOKEN.equals(trimmed)) {
            throw new BadRequestException(
                    "'" + ONE_SIZE_TOKEN + "' is reserved for single-size products.");
        }

        // SELECT FOR UPDATE on the product so a concurrent is_multi_size flip can't interleave
        // between our guard and the INSERT below.
        ProductEntity product = productRepository.findByIdWithLock(productId)
                .orElseThrow(() -> new NotFoundException("Product " + productId + " not found."));
        if (!product.isMultiSize()) {
            throw new BadRequestException(
                    "Product " + productId + " is single-size; cannot add additional sizes.");
        }
        try {
            productStockRepository.adminInsert(productId, trimmed, quantity, threshold);
        } catch (DataIntegrityViolationException ex) {
            throw sizeAlreadyExists(productId, trimmed);
        }
        return readRowOrThrow(productId, trimmed);
    }

    @Transactional
    public void removeSize(Long productId, String size) {
        if (ONE_SIZE_TOKEN.equals(size)) {
            throw new BadRequestException(
                    "'" + ONE_SIZE_TOKEN + "' cannot be removed.");
        }
        ProductEntity product = productRepository.findById(productId)
                .orElseThrow(() -> new NotFoundException("Product " + productId + " not found."));
        if (!product.isMultiSize()) {
            throw new BadRequestException(
                    "Product " + productId + " is single-size; cannot remove sizes.");
        }
        // Idempotent: 0 rows means the size was already removed; surface as success per swagger.
        productStockRepository.adminDelete(productId, size);
    }

    private StockRowDto readRowOrThrow(Long productId, String size) {
        return productStockRepository.findById(new ProductStockId(productId, size))
                .map(productStockEntityToStockRowDtoMapper::map)
                .orElseThrow(() -> stockRowNotFound(productId, size));
    }

    private static NotFoundException stockRowNotFound(Long productId, String size) {
        return new NotFoundException(
                "Stock row for product " + productId + " size '" + size + "' not found.");
    }

    private static ConflictException sizeAlreadyExists(Long productId, String size) {
        return new ConflictException(
                "Size '" + size + "' already exists for product " + productId + ".");
    }

    private Sort buildSort(String sortField, String sortDirection) {
        Sort.Direction direction = SORT_DIRECTION_DESC.equalsIgnoreCase(sortDirection)
                ? Sort.Direction.DESC
                : Sort.Direction.ASC;
        Sort.Order sizeTie = Sort.Order.asc(SORT_TIEBREAK_PATH_SIZE);
        Sort.Order productIdTie = Sort.Order.asc(SORT_TIEBREAK_PATH_PRODUCT_ID);
        String primary = resolvePrimarySortPath(sortField);
        return Sort.by(new Sort.Order(direction, primary), sizeTie, productIdTie);
    }

    private String resolvePrimarySortPath(String sortField) {
        if (sortField == null || sortField.isBlank()) return SORT_PATH_PRODUCT_NAME;
        if (SORT_FIELD_NAME.equalsIgnoreCase(sortField)) return SORT_PATH_PRODUCT_NAME;
        if (SORT_FIELD_QUANTITY.equalsIgnoreCase(sortField)) return SORT_PATH_QUANTITY;
        if (SORT_FIELD_THRESHOLD.equalsIgnoreCase(sortField)) return SORT_PATH_THRESHOLD;
        return SORT_PATH_PRODUCT_NAME;
    }
}
