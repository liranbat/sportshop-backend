package com.java.sadna.backend.sportshop.mapper;

import com.java.sadna.backend.sportshop.api.generated.cart.model.VersionMismatch;
import com.java.sadna.backend.sportshop.model.VersionMismatchDto;
import org.springframework.stereotype.Component;

@Component
public class VersionMismatchDtoToVersionMismatchMapper implements BaseMapper<VersionMismatchDto, VersionMismatch> {

    @Override
    public VersionMismatch map(VersionMismatchDto source) {
        return new VersionMismatch()
                .productId(source.getProductId())
                .productName(source.getProductName())
                .size(source.getSize())
                .productIsArchived(source.isProductIsArchived());
    }
}
