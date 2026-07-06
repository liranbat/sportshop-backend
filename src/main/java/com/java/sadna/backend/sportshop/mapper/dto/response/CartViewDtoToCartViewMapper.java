package com.java.sadna.backend.sportshop.mapper.dto.response;

import com.java.sadna.backend.sportshop.api.generated.cart.model.CartItem;
import com.java.sadna.backend.sportshop.api.generated.cart.model.CartView;
import com.java.sadna.backend.sportshop.mapper.BaseMapper;
import com.java.sadna.backend.sportshop.model.CartViewDto;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class CartViewDtoToCartViewMapper implements BaseMapper<CartViewDto, CartView> {

    private final CartItemDtoToCartItemMapper cartItemDtoToCartItemMapper;

    public CartViewDtoToCartViewMapper(CartItemDtoToCartItemMapper cartItemDtoToCartItemMapper) {
        this.cartItemDtoToCartItemMapper = cartItemDtoToCartItemMapper;
    }

    @Override
    public CartView map(CartViewDto source) {
        List<CartItem> items = source.getItems().stream()
                .map(cartItemDtoToCartItemMapper::map)
                .toList();
        return new CartView()
                .items(items)
                .itemCount(source.getItemCount())
                .subtotal(source.getSubtotal());
    }
}
