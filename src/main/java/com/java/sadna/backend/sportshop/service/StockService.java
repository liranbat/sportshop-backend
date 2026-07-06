package com.java.sadna.backend.sportshop.service;

import com.java.sadna.backend.sportshop.api.generated.stock.model.StockArchiveStatusFilter;
import com.java.sadna.backend.sportshop.api.generated.stock.model.StockStatusFilter;
import com.java.sadna.backend.sportshop.common.constants.ProductConstants;
import com.java.sadna.backend.sportshop.common.util.SortDirections;
import com.java.sadna.backend.sportshop.common.util.SortResolver;
import com.java.sadna.backend.sportshop.config.PaginationProperties;
import com.java.sadna.backend.sportshop.entity.ProductEntity;
import com.java.sadna.backend.sportshop.entity.ProductStockEntity;
import com.java.sadna.backend.sportshop.entity.id.ProductStockId;
import com.java.sadna.backend.sportshop.exception.BadRequestException;
import com.java.sadna.backend.sportshop.exception.ConflictException;
import com.java.sadna.backend.sportshop.exception.NotFoundException;
import com.java.sadna.backend.sportshop.mapper.entity.dto.ProductStockEntityToStockRowDtoMapper;
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
import java.util.Map;

@Service
public class StockService {

    private static final SortResolver SORT_RESOLVER = new SortResolver(
            Map.of(
                    "name", List.of("product.name"),
                    "quantity", List.of("quantity"),
                    "threshold", List.of("lowStockThreshold")
            ),
            SortResolver.orders("product.name", SortDirections.ASC, "size", SortDirections.ASC, "productId", SortDirections.ASC),
            SortResolver.orders("size", SortDirections.ASC, "productId", SortDirections.ASC)
    );

    private final ProductStockRepository productStockRepository;
    private final ProductRepository productRepository;
    private final PaginationService paginationService;
    private final ProductStockEntityToStockRowDtoMapper productStockEntityToStockRowDtoMapper;
    private final int defaultPageSize;

    public StockService(ProductStockRepository productStockRepository,
                        ProductRepository productRepository,
                        PaginationService paginationService,
                        ProductStockEntityToStockRowDtoMapper productStockEntityToStockRowDtoMapper,
                        PaginationProperties paginationProperties) {
        this.productStockRepository = productStockRepository;
        this.productRepository = productRepository;
        this.paginationService = paginationService;
        this.productStockEntityToStockRowDtoMapper = productStockEntityToStockRowDtoMapper;
        this.defaultPageSize = paginationProperties.getDefaultPageSize()
                .getOrDefault("stock", 50);
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
        Sort sort = SORT_RESOLVER.resolve(sortField, sortDirection);
        return paginationService.paginate(
                productStockRepository, spec, sort, page, pageSize, defaultPageSize,
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
            throw new BadRequestException("stock.deltaNonZero");
        }
        int affected = productStockRepository.adminAdjust(productId, size, delta);
        if (affected == 0) {
            throw new ConflictException("stock.adjustConflict", delta);
        }
        return readRowOrThrow(productId, size);
    }

    @Transactional
    public StockRowDto addSize(Long productId, String size, int quantity, Integer threshold) {
        String trimmed = size == null ? "" : size.trim();
        if (trimmed.isEmpty()) {
            throw new BadRequestException("stock.sizeBlank");
        }
        if (ProductConstants.ONE_SIZE_TOKEN.equals(trimmed)) {
            throw new BadRequestException("stock.oneSizeReserved", ProductConstants.ONE_SIZE_TOKEN);
        }

        // SELECT FOR UPDATE on the product so a concurrent is_multi_size flip can't interleave
        // between our guard and the INSERT below.
        ProductEntity product = productRepository.findByIdWithLock(productId)
                .orElseThrow(() -> new NotFoundException("product.notFound", productId));
        if (!product.isMultiSize()) {
            throw new BadRequestException("stock.multiSize.cannotAdd", productId);
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
        if (ProductConstants.ONE_SIZE_TOKEN.equals(size)) {
            throw new BadRequestException("stock.oneSizeNotRemovable", ProductConstants.ONE_SIZE_TOKEN);
        }
        ProductEntity product = productRepository.findById(productId)
                .orElseThrow(() -> new NotFoundException("product.notFound", productId));
        if (!product.isMultiSize()) {
            throw new BadRequestException("stock.multiSize.cannotRemove", productId);
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
        return new NotFoundException("stock.rowNotFound", productId, size);
    }

    private static ConflictException sizeAlreadyExists(Long productId, String size) {
        return new ConflictException("stock.sizeAlreadyExists", productId, size);
    }
}
