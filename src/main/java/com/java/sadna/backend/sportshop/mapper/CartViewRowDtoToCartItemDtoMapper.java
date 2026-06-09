package com.java.sadna.backend.sportshop.mapper;

import com.java.sadna.backend.sportshop.model.CartItemDto;
import com.java.sadna.backend.sportshop.model.CartViewRowDto;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
public class CartViewRowDtoToCartItemDtoMapper implements BaseMapper<CartViewRowDto, CartItemDto> {

    @Override
    public CartItemDto map(CartViewRowDto row) {
        BigDecimal price = row.getProductPrice() != null ? row.getProductPrice() : BigDecimal.ZERO;
        int quantity = row.getQuantity() != null ? row.getQuantity() : 0;
        int availableStock = row.getAvailableStock() != null ? row.getAvailableStock() : 0;
        boolean archived = Boolean.TRUE.equals(row.getProductArchived());
        int versionInCart = row.getProductVersionInCart() != null ? row.getProductVersionInCart() : 0;
        int versionCurrent = row.getProductVersionCurrent() != null ? row.getProductVersionCurrent() : versionInCart;
        BigDecimal lineTotal = price.multiply(BigDecimal.valueOf(quantity));

        return new CartItemDto(
                row.getProductId(),
                row.getSize(),
                quantity,
                row.getProductName(),
                row.getProductImageFilename(),
                price,
                row.getCategoryName(),
                archived,
                versionInCart,
                versionCurrent,
                availableStock,
                row.getLowStockThreshold(),
                lineTotal
        );
    }
}
