package com.java.sadna.backend.sportshop.mapper;

import com.java.sadna.backend.sportshop.api.generated.cart.model.CartItem;
import com.java.sadna.backend.sportshop.config.AppProperties;
import com.java.sadna.backend.sportshop.config.ImagesProperties;
import com.java.sadna.backend.sportshop.model.CartItemDto;
import org.springframework.stereotype.Component;

@Component
public class CartItemDtoToCartItemMapper implements BaseMapper<CartItemDto, CartItem> {

    private final ImagesProperties imagesProperties;

    public CartItemDtoToCartItemMapper(AppProperties appProperties) {
        this.imagesProperties = appProperties.getImages();
    }

    @Override
    public CartItem map(CartItemDto source) {
        return new CartItem()
                .productId(source.getProductId())
                .size(source.getSize())
                .quantity(source.getQuantity())
                .productName(source.getProductName())
                .productImageUrl(imagesProperties.getProductImageUrl(source.getProductImageFilename()))
                .productPrice(source.getProductPrice())
                .productCategoryName(source.getProductCategoryName())
                .productIsArchived(source.isProductIsArchived())
                .productVersionInCart(source.getProductVersionInCart())
                .productVersionCurrent(source.getProductVersionCurrent())
                .availableStock(source.getAvailableStock())
                .lowStockThreshold(source.getLowStockThreshold())
                .lineTotal(source.getLineTotal());
    }
}
