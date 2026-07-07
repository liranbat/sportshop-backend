package com.java.sadna.backend.sportshop.mapper.dto.response;

import com.java.sadna.backend.sportshop.api.generated.cart.model.CartValidationResult;
import com.java.sadna.backend.sportshop.mapper.BaseMapper;
import com.java.sadna.backend.sportshop.model.CartValidationResultDto;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring",
        uses = {
                VersionMismatchDtoToVersionMismatchMapper.class,
                StockIssueDtoToStockIssueMapper.class,
                CartViewDtoToCartViewMapper.class
        })
public interface CartValidationResultDtoToCartValidationResultMapper
        extends BaseMapper<CartValidationResultDto, CartValidationResult> {

    @Override
    CartValidationResult map(CartValidationResultDto source);
}
