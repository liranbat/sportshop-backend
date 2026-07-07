package com.java.sadna.backend.sportshop.mapper.dto.response;

import com.java.sadna.backend.sportshop.api.generated.cart.model.CartView;
import com.java.sadna.backend.sportshop.mapper.BaseMapper;
import com.java.sadna.backend.sportshop.model.CartViewDto;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring", uses = CartItemDtoToCartItemMapper.class)
public interface CartViewDtoToCartViewMapper extends BaseMapper<CartViewDto, CartView> {

    @Override
    CartView map(CartViewDto source);
}
