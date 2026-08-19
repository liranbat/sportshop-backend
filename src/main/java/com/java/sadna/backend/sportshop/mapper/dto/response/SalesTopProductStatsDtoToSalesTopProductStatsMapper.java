package com.java.sadna.backend.sportshop.mapper.dto.response;

import com.java.sadna.backend.sportshop.api.generated.sales.model.SalesTopProductStats;
import com.java.sadna.backend.sportshop.config.ImagesProperties;
import com.java.sadna.backend.sportshop.mapper.BaseMapper;
import com.java.sadna.backend.sportshop.model.SalesTopProductStatsDto;
import org.springframework.stereotype.Component;

@Component
public class SalesTopProductStatsDtoToSalesTopProductStatsMapper
        implements BaseMapper<SalesTopProductStatsDto, SalesTopProductStats> {

    private final ImagesProperties imagesProperties;

    public SalesTopProductStatsDtoToSalesTopProductStatsMapper(ImagesProperties imagesProperties) {
        this.imagesProperties = imagesProperties;
    }

    @Override
    public SalesTopProductStats map(SalesTopProductStatsDto source) {
        return new SalesTopProductStats()
                .productId(source.getProductId())
                .productName(source.getProductName())
                .productImageUrl(imagesProperties.getProductImageUrl(source.getProductImageFilename()))
                .result(source.getResult());
    }
}
