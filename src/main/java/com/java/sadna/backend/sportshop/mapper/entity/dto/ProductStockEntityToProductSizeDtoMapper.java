package com.java.sadna.backend.sportshop.mapper.entity.dto;

import com.java.sadna.backend.sportshop.entity.ProductStockEntity;
import com.java.sadna.backend.sportshop.mapper.BaseMapper;
import com.java.sadna.backend.sportshop.model.ProductSizeDto;
import com.java.sadna.backend.sportshop.model.enums.StockState;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

@Mapper(componentModel = "spring")
public interface ProductStockEntityToProductSizeDtoMapper extends BaseMapper<ProductStockEntity, ProductSizeDto> {

    @Override
    @Mapping(target = "state", source = ".", qualifiedByName = "computeState")
    ProductSizeDto map(ProductStockEntity entity);

    @Named("computeState")
    default StockState computeState(ProductStockEntity entity) {
        int quantity = entity.getQuantity();
        // NULL threshold = opt out of the LOW_STOCK band entirely (effectively 0), so
        // any positive quantity surfaces as IN_STOCK.
        int threshold = entity.getLowStockThreshold() != null ? entity.getLowStockThreshold() : 0;
        if (quantity == 0) {
            return StockState.OUT_OF_STOCK;
        }
        if (quantity <= threshold) {
            return StockState.LOW_STOCK;
        }
        return StockState.IN_STOCK;
    }
}
