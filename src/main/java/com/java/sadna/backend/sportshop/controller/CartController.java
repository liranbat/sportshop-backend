package com.java.sadna.backend.sportshop.controller;

import com.java.sadna.backend.sportshop.api.generated.cart.api.CartApi;
import com.java.sadna.backend.sportshop.api.generated.cart.model.AddCartItemRequest;
import com.java.sadna.backend.sportshop.api.generated.cart.model.CartCount;
import com.java.sadna.backend.sportshop.api.generated.cart.model.CartValidationResult;
import com.java.sadna.backend.sportshop.api.generated.cart.model.CartView;
import com.java.sadna.backend.sportshop.api.generated.cart.model.UpdateCartItemRequest;
import com.java.sadna.backend.sportshop.mapper.dto.response.CartCountDtoToCartCountMapper;
import com.java.sadna.backend.sportshop.mapper.dto.response.CartValidationResultDtoToCartValidationResultMapper;
import com.java.sadna.backend.sportshop.mapper.dto.response.CartViewDtoToCartViewMapper;
import com.java.sadna.backend.sportshop.security.AuthorityRules;
import com.java.sadna.backend.sportshop.security.SecurityContextUtils;
import com.java.sadna.backend.sportshop.service.CartService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class CartController implements CartApi {

    private final CartService cartService;
    private final CartCountDtoToCartCountMapper cartCountDtoToCartCountMapper;
    private final CartViewDtoToCartViewMapper cartViewDtoToCartViewMapper;
    private final CartValidationResultDtoToCartValidationResultMapper cartValidationResultDtoToCartValidationResultMapper;

    public CartController(CartService cartService,
                          CartCountDtoToCartCountMapper cartCountDtoToCartCountMapper,
                          CartViewDtoToCartViewMapper cartViewDtoToCartViewMapper,
                          CartValidationResultDtoToCartValidationResultMapper cartValidationResultDtoToCartValidationResultMapper) {
        this.cartService = cartService;
        this.cartCountDtoToCartCountMapper = cartCountDtoToCartCountMapper;
        this.cartViewDtoToCartViewMapper = cartViewDtoToCartViewMapper;
        this.cartValidationResultDtoToCartValidationResultMapper = cartValidationResultDtoToCartValidationResultMapper;
    }

    @Override
    @PreAuthorize(AuthorityRules.AUTHENTICATED)
    public ResponseEntity<CartCount> getCartCount() {
        Long userId = SecurityContextUtils.currentUserIdOrThrow();
        return ResponseEntity.ok(cartCountDtoToCartCountMapper.map(cartService.getCount(userId)));
    }

    @Override
    @PreAuthorize(AuthorityRules.AUTHENTICATED)
    public ResponseEntity<CartView> getCart() {
        Long userId = SecurityContextUtils.currentUserIdOrThrow();
        return ResponseEntity.ok(cartViewDtoToCartViewMapper.map(cartService.read(userId)));
    }

    @Override
    @PreAuthorize(AuthorityRules.AUTHENTICATED)
    public ResponseEntity<CartView> syncCart() {
        Long userId = SecurityContextUtils.currentUserIdOrThrow();
        return ResponseEntity.ok(cartViewDtoToCartViewMapper.map(cartService.sync(userId)));
    }

    @Override
    @PreAuthorize(AuthorityRules.AUTHENTICATED)
    public ResponseEntity<CartValidationResult> validateCart() {
        Long userId = SecurityContextUtils.currentUserIdOrThrow();
        return ResponseEntity.ok(
                cartValidationResultDtoToCartValidationResultMapper.map(cartService.validateForCheckout(userId))
        );
    }

    @Override
    @PreAuthorize(AuthorityRules.AUTHENTICATED)
    public ResponseEntity<Void> addCartItem(AddCartItemRequest addCartItemRequest) {
        Long userId = SecurityContextUtils.currentUserIdOrThrow();
        cartService.addItem(
                userId,
                addCartItemRequest.getProductId(),
                addCartItemRequest.getSize(),
                addCartItemRequest.getQuantity()
        );
        return ResponseEntity.noContent().build();
    }

    @Override
    @PreAuthorize(AuthorityRules.AUTHENTICATED)
    public ResponseEntity<Void> updateCartItem(Long productId, String size,
                                               UpdateCartItemRequest updateCartItemRequest) {
        Long userId = SecurityContextUtils.currentUserIdOrThrow();
        cartService.updateQuantity(userId, productId, size, updateCartItemRequest.getQuantity());
        return ResponseEntity.noContent().build();
    }

    @Override
    @PreAuthorize(AuthorityRules.AUTHENTICATED)
    public ResponseEntity<Void> removeCartItem(Long productId, String size) {
        Long userId = SecurityContextUtils.currentUserIdOrThrow();
        cartService.removeItem(userId, productId, size);
        return ResponseEntity.noContent().build();
    }
}
