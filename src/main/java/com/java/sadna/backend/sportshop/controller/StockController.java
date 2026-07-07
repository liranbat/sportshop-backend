package com.java.sadna.backend.sportshop.controller;

import com.java.sadna.backend.sportshop.api.generated.stock.api.AdminStockApi;
import com.java.sadna.backend.sportshop.api.generated.stock.model.StockAdjustRequest;
import com.java.sadna.backend.sportshop.api.generated.stock.model.StockArchiveStatusFilter;
import com.java.sadna.backend.sportshop.api.generated.stock.model.StockPage;
import com.java.sadna.backend.sportshop.api.generated.stock.model.StockRow;
import com.java.sadna.backend.sportshop.api.generated.stock.model.StockSetRequest;
import com.java.sadna.backend.sportshop.api.generated.stock.model.StockSizeAddRequest;
import com.java.sadna.backend.sportshop.api.generated.stock.model.StockStatusFilter;
import com.java.sadna.backend.sportshop.mapper.dto.response.PagedStockRowDtoToStockPageMapper;
import com.java.sadna.backend.sportshop.mapper.dto.response.StockRowDtoToStockRowMapper;
import com.java.sadna.backend.sportshop.model.PagedResult;
import com.java.sadna.backend.sportshop.model.StockRowDto;
import com.java.sadna.backend.sportshop.security.AuthorityRules;
import com.java.sadna.backend.sportshop.service.StockService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class StockController implements AdminStockApi {

    private final StockService stockService;
    private final StockRowDtoToStockRowMapper stockRowDtoToStockRowMapper;
    private final PagedStockRowDtoToStockPageMapper pagedStockRowDtoToStockPageMapper;

    public StockController(StockService stockService,
                           StockRowDtoToStockRowMapper stockRowDtoToStockRowMapper,
                           PagedStockRowDtoToStockPageMapper pagedStockRowDtoToStockPageMapper) {
        this.stockService = stockService;
        this.stockRowDtoToStockRowMapper = stockRowDtoToStockRowMapper;
        this.pagedStockRowDtoToStockPageMapper = pagedStockRowDtoToStockPageMapper;
    }

    @Override
    @PreAuthorize(AuthorityRules.ADMIN)
    public ResponseEntity<StockPage> listAdminStock(String searchName,
                                                    List<String> sizes,
                                                    StockStatusFilter stockStatus,
                                                    StockArchiveStatusFilter archiveStatus,
                                                    String sortField,
                                                    String sortDirection,
                                                    Integer page,
                                                    Integer pageSize) {
        PagedResult<StockRowDto> result = stockService.list(
                searchName, sizes, stockStatus, archiveStatus,
                sortField, sortDirection, page, pageSize
        );
        return ResponseEntity.ok(pagedStockRowDtoToStockPageMapper.map(result));
    }

    @Override
    @PreAuthorize(AuthorityRules.ADMIN)
    public ResponseEntity<StockRow> setAdminStock(Long productId,
                                                  String size,
                                                  StockSetRequest body) {
        StockRowDto dto = stockService.setStock(
                productId, size, body.getQuantity(), body.getLowStockThreshold()
        );
        return ResponseEntity.ok(stockRowDtoToStockRowMapper.map(dto));
    }

    @Override
    @PreAuthorize(AuthorityRules.ADMIN)
    public ResponseEntity<StockRow> adjustAdminStock(Long productId,
                                                     String size,
                                                     StockAdjustRequest body) {
        StockRowDto dto = stockService.adjustQuantity(productId, size, body.getDelta());
        return ResponseEntity.ok(stockRowDtoToStockRowMapper.map(dto));
    }

    @Override
    @PreAuthorize(AuthorityRules.ADMIN)
    public ResponseEntity<StockRow> addAdminStockSize(Long productId, StockSizeAddRequest body) {
        StockRowDto dto = stockService.addSize(
                productId, body.getSize(), body.getQuantity(), body.getLowStockThreshold()
        );
        return ResponseEntity.ok(stockRowDtoToStockRowMapper.map(dto));
    }

    @Override
    @PreAuthorize(AuthorityRules.ADMIN)
    public ResponseEntity<Void> removeAdminStockSize(Long productId, String size) {
        stockService.removeSize(productId, size);
        return ResponseEntity.noContent().build();
    }
}
