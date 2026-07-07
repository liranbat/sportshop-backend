package com.java.sadna.backend.sportshop.mapper.dto.response;

import com.java.sadna.backend.sportshop.api.generated.cart.model.CartCount;
import com.java.sadna.backend.sportshop.mapper.BaseMapper;
import com.java.sadna.backend.sportshop.model.CartCountDto;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface CartCountDtoToCartCountMapper extends BaseMapper<CartCountDto, CartCount> {

    @Override
    CartCount map(CartCountDto source);
}
