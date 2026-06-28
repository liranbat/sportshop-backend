package com.java.sadna.backend.sportshop.mapper;

import com.java.sadna.backend.sportshop.entity.ProductEntity;
import com.java.sadna.backend.sportshop.entity.ProductStockEntity;
import com.java.sadna.backend.sportshop.model.StockRowDto;
import org.springframework.stereotype.Component;

@Component
public class ProductStockEntityToStockRowDtoMapper implements BaseMapper<ProductStockEntity, StockRowDto> {

    @Override
    public StockRowDto map(ProductStockEntity stock) {
        ProductEntity product = stock.getProduct();
        return new StockRowDto(
                stock.getProductId(),
                product.getName(),
                product.getImageFilename(),
                product.isArchived(),
                product.isMultiSize(),
                stock.getSize(),
                stock.getQuantity(),
                stock.getLowStockThreshold()
        );
    }
}
