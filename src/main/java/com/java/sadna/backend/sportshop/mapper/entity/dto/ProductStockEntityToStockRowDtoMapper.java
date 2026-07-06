package com.java.sadna.backend.sportshop.mapper.entity.dto;

import com.java.sadna.backend.sportshop.entity.ProductStockEntity;
import com.java.sadna.backend.sportshop.mapper.BaseMapper;
import com.java.sadna.backend.sportshop.model.StockRowDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ProductStockEntityToStockRowDtoMapper extends BaseMapper<ProductStockEntity, StockRowDto> {

    @Override
    @Mapping(source = "product.name", target = "productName")
    @Mapping(source = "product.imageFilename", target = "productImageFilename")
    @Mapping(source = "product.archived", target = "productIsArchived")
    @Mapping(source = "product.multiSize", target = "productIsMultiSize")
    StockRowDto map(ProductStockEntity stock);
}
