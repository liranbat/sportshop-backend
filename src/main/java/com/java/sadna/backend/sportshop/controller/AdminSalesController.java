package com.java.sadna.backend.sportshop.controller;

import com.java.sadna.backend.sportshop.api.generated.sales.api.AdminSalesApi;
import com.java.sadna.backend.sportshop.api.generated.sales.model.SalesSummary;
import com.java.sadna.backend.sportshop.common.constants.AuthorityConstants;
import com.java.sadna.backend.sportshop.mapper.dto.response.SalesSummaryDtoToSalesSummaryMapper;
import com.java.sadna.backend.sportshop.service.SalesService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;

@RestController
public class AdminSalesController implements AdminSalesApi {

    private final SalesService salesService;
    private final SalesSummaryDtoToSalesSummaryMapper salesSummaryDtoToSalesSummaryMapper;

    public AdminSalesController(SalesService salesService,
                                SalesSummaryDtoToSalesSummaryMapper salesSummaryDtoToSalesSummaryMapper) {
        this.salesService = salesService;
        this.salesSummaryDtoToSalesSummaryMapper = salesSummaryDtoToSalesSummaryMapper;
    }

    @Override
    @PreAuthorize(AuthorityConstants.ADMIN)
    public ResponseEntity<SalesSummary> getAdminSalesSummary(LocalDate dateFrom,
                                                             LocalDate dateTo,
                                                             String topProductsSortBy) {
        return ResponseEntity.ok(salesSummaryDtoToSalesSummaryMapper.map(
                salesService.getSalesSummary(dateFrom, dateTo, topProductsSortBy)
        ));
    }
}
