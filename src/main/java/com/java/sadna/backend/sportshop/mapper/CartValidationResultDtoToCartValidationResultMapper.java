package com.java.sadna.backend.sportshop.mapper;

import com.java.sadna.backend.sportshop.api.generated.cart.model.CartValidationResult;
import com.java.sadna.backend.sportshop.api.generated.cart.model.StockIssue;
import com.java.sadna.backend.sportshop.api.generated.cart.model.VersionMismatch;
import com.java.sadna.backend.sportshop.model.CartValidationResultDto;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class CartValidationResultDtoToCartValidationResultMapper
        implements BaseMapper<CartValidationResultDto, CartValidationResult> {

    private final VersionMismatchDtoToVersionMismatchMapper versionMismatchDtoToVersionMismatchMapper;
    private final StockIssueDtoToStockIssueMapper stockIssueDtoToStockIssueMapper;
    private final CartViewDtoToCartViewMapper cartViewDtoToCartViewMapper;

    public CartValidationResultDtoToCartValidationResultMapper(
            VersionMismatchDtoToVersionMismatchMapper versionMismatchDtoToVersionMismatchMapper,
            StockIssueDtoToStockIssueMapper stockIssueDtoToStockIssueMapper,
            CartViewDtoToCartViewMapper cartViewDtoToCartViewMapper) {
        this.versionMismatchDtoToVersionMismatchMapper = versionMismatchDtoToVersionMismatchMapper;
        this.stockIssueDtoToStockIssueMapper = stockIssueDtoToStockIssueMapper;
        this.cartViewDtoToCartViewMapper = cartViewDtoToCartViewMapper;
    }

    @Override
    public CartValidationResult map(CartValidationResultDto source) {
        List<VersionMismatch> versionMismatches = source.getVersionMismatches().stream()
                .map(versionMismatchDtoToVersionMismatchMapper::map)
                .toList();
        List<StockIssue> stockIssues = source.getStockIssues().stream()
                .map(stockIssueDtoToStockIssueMapper::map)
                .toList();
        return new CartValidationResult()
                .ok(source.isOk())
                .versionMismatches(versionMismatches)
                .stockIssues(stockIssues)
                .cart(cartViewDtoToCartViewMapper.map(source.getCart()));
    }
}
