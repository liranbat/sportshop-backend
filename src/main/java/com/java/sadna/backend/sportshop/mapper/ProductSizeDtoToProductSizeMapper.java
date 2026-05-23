package com.java.sadna.backend.sportshop.mapper;

import com.java.sadna.backend.sportshop.api.generated.products.model.ProductSize;
import com.java.sadna.backend.sportshop.model.ProductSizeDto;
import com.java.sadna.backend.sportshop.model.StockState;
import org.springframework.stereotype.Component;

@Component
public class ProductSizeDtoToProductSizeMapper implements BaseMapper<ProductSizeDto, ProductSize> {

    @Override
    public ProductSize map(ProductSizeDto dto) {
        return new ProductSize()
                .size(dto.getSize())
                .quantity(dto.getQuantity())
                .state(mapState(dto.getState()));
    }

    private static ProductSize.StateEnum mapState(StockState state) {
        return switch (state) {
            case IN_STOCK -> ProductSize.StateEnum.IN_STOCK;
            case LOW_STOCK -> ProductSize.StateEnum.LOW_STOCK;
            case OUT_OF_STOCK -> ProductSize.StateEnum.OUT_OF_STOCK;
        };
    }
}
