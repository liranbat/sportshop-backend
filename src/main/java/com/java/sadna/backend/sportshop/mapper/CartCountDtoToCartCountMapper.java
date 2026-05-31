package com.java.sadna.backend.sportshop.mapper;

import com.java.sadna.backend.sportshop.api.generated.cart.model.CartCount;
import com.java.sadna.backend.sportshop.model.CartCountDto;
import org.springframework.stereotype.Component;

@Component
public class CartCountDtoToCartCountMapper implements BaseMapper<CartCountDto, CartCount> {

    @Override
    public CartCount map(CartCountDto source) {
        return new CartCount().itemCount(source.getItemCount());
    }
}
