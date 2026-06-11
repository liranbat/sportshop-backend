package com.java.sadna.backend.sportshop.controller;

import com.java.sadna.backend.sportshop.api.generated.checkout.api.CheckoutApi;
import com.java.sadna.backend.sportshop.api.generated.checkout.model.CheckoutRequest;
import com.java.sadna.backend.sportshop.api.generated.checkout.model.CheckoutResult;
import com.java.sadna.backend.sportshop.mapper.CheckoutRequestToCheckoutRequestDtoMapper;
import com.java.sadna.backend.sportshop.mapper.CheckoutResultDtoToCheckoutResultMapper;
import com.java.sadna.backend.sportshop.security.SecurityContextUtils;
import com.java.sadna.backend.sportshop.service.CheckoutService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class CheckoutController implements CheckoutApi {

    private final CheckoutService checkoutService;
    private final CheckoutRequestToCheckoutRequestDtoMapper checkoutRequestToCheckoutRequestDtoMapper;
    private final CheckoutResultDtoToCheckoutResultMapper checkoutResultDtoToCheckoutResultMapper;

    public CheckoutController(CheckoutService checkoutService,
                              CheckoutRequestToCheckoutRequestDtoMapper checkoutRequestToCheckoutRequestDtoMapper,
                              CheckoutResultDtoToCheckoutResultMapper checkoutResultDtoToCheckoutResultMapper) {
        this.checkoutService = checkoutService;
        this.checkoutRequestToCheckoutRequestDtoMapper = checkoutRequestToCheckoutRequestDtoMapper;
        this.checkoutResultDtoToCheckoutResultMapper = checkoutResultDtoToCheckoutResultMapper;
    }

    @Override
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<CheckoutResult> checkout(CheckoutRequest checkoutRequest) {
        Long userId = SecurityContextUtils.currentUserIdOrThrow();
        return ResponseEntity.ok(checkoutResultDtoToCheckoutResultMapper.map(
                checkoutService.checkout(userId, checkoutRequestToCheckoutRequestDtoMapper.map(checkoutRequest))
        ));
    }
}
