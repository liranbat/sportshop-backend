package com.java.sadna.backend.sportshop.mapper.request.dto;

import com.java.sadna.backend.sportshop.api.generated.products.model.ProductCreateRequest;
import com.java.sadna.backend.sportshop.api.generated.products.model.ProductStockEntry;
import com.java.sadna.backend.sportshop.mapper.BaseMapper;
import com.java.sadna.backend.sportshop.model.ProductCreateRequestDto;
import com.java.sadna.backend.sportshop.model.ProductStockInputDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.util.LinkedHashMap;
import java.util.Map;

@Mapper(componentModel = "spring")
public interface ProductCreateRequestToProductCreateRequestDtoMapper
        extends BaseMapper<ProductCreateRequest, ProductCreateRequestDto> {

    @Override
    @Mapping(target = "multiSize", expression = "java(Boolean.TRUE.equals(source.getIsMultiSize()))")
    @Mapping(target = "stockBySize", qualifiedByName = "toStockInputMap")
    ProductCreateRequestDto map(ProductCreateRequest source);

    @Named("toStockInputMap")
    default Map<String, ProductStockInputDto> toStockInputMap(Map<String, ProductStockEntry> stockBySize) {
        if (stockBySize == null) {
            return Map.of();
        }
        Map<String, ProductStockInputDto> result = new LinkedHashMap<>(stockBySize.size());
        stockBySize.forEach((size, entry) ->
                result.put(size, new ProductStockInputDto(entry.getQuantity(), entry.getLowStockThreshold())));
        return result;
    }
}
