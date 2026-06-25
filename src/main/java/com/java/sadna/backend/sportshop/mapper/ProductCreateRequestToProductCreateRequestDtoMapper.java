package com.java.sadna.backend.sportshop.mapper;

import com.java.sadna.backend.sportshop.api.generated.products.model.ProductCreateRequest;
import com.java.sadna.backend.sportshop.api.generated.products.model.ProductStockEntry;
import com.java.sadna.backend.sportshop.model.ProductCreateRequestDto;
import com.java.sadna.backend.sportshop.model.ProductStockInputDto;
import org.springframework.stereotype.Component;

import java.util.LinkedHashMap;
import java.util.Map;

@Component
public class ProductCreateRequestToProductCreateRequestDtoMapper
        implements BaseMapper<ProductCreateRequest, ProductCreateRequestDto> {

    @Override
    public ProductCreateRequestDto map(ProductCreateRequest source) {
        return new ProductCreateRequestDto(
                source.getName(),
                source.getDescription(),
                source.getCategoryId(),
                Boolean.TRUE.equals(source.getIsMultiSize()),
                source.getImageUrl(),
                source.getPrice(),
                toStockInputMap(source.getStockBySize())
        );
    }

    private static Map<String, ProductStockInputDto> toStockInputMap(Map<String, ProductStockEntry> stockBySize) {
        if (stockBySize == null) {
            return Map.of();
        }
        Map<String, ProductStockInputDto> result = new LinkedHashMap<>(stockBySize.size());
        stockBySize.forEach((size, entry) ->
                result.put(size, new ProductStockInputDto(entry.getQuantity(), entry.getLowStockThreshold())));
        return result;
    }
}
