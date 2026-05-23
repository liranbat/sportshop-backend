package com.java.sadna.backend.sportshop.mapper;

import com.java.sadna.backend.sportshop.entity.ProductStockEntity;
import com.java.sadna.backend.sportshop.model.ProductSizeDto;
import com.java.sadna.backend.sportshop.model.StockState;
import org.springframework.stereotype.Component;

@Component
public class ProductStockEntityToProductSizeDtoMapper implements BaseMapper<ProductStockEntity, ProductSizeDto> {

    @Override
    public ProductSizeDto map(ProductStockEntity entity) {
        int quantity = entity.getQuantity();
        // NULL threshold = opt out of the LOW_STOCK band entirely (effectively 0), so
        // any positive quantity surfaces as IN_STOCK.
        int threshold = entity.getLowStockThreshold() != null ? entity.getLowStockThreshold() : 0;
        return new ProductSizeDto(entity.getSize(), quantity, computeState(quantity, threshold));
    }

    private static StockState computeState(int quantity, int threshold) {
        if (quantity == 0) {
            return StockState.OUT_OF_STOCK;
        }
        if (quantity <= threshold) {
            return StockState.LOW_STOCK;
        }
        return StockState.IN_STOCK;
    }
}
