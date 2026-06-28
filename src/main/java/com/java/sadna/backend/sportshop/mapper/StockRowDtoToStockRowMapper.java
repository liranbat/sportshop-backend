package com.java.sadna.backend.sportshop.mapper;

import com.java.sadna.backend.sportshop.api.generated.stock.model.StockRow;
import com.java.sadna.backend.sportshop.config.AppProperties;
import com.java.sadna.backend.sportshop.config.ImagesProperties;
import com.java.sadna.backend.sportshop.model.StockRowDto;
import org.springframework.stereotype.Component;

@Component
public class StockRowDtoToStockRowMapper implements BaseMapper<StockRowDto, StockRow> {

    private final ImagesProperties imagesProperties;

    public StockRowDtoToStockRowMapper(AppProperties appProperties) {
        this.imagesProperties = appProperties.getImages();
    }

    @Override
    public StockRow map(StockRowDto dto) {
        return new StockRow()
                .productId(dto.getProductId())
                .productName(dto.getProductName())
                .productImageUrl(imagesProperties.getProductImageUrl(dto.getProductImageFilename()))
                .productIsArchived(dto.isProductIsArchived())
                .productIsMultiSize(dto.isProductIsMultiSize())
                .size(dto.getSize())
                .quantity(dto.getQuantity())
                .lowStockThreshold(dto.getLowStockThreshold());
    }
}
