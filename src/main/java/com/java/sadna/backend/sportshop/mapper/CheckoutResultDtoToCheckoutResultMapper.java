package com.java.sadna.backend.sportshop.mapper;

import com.java.sadna.backend.sportshop.api.generated.checkout.model.CheckoutResult;
import com.java.sadna.backend.sportshop.model.CheckoutResultDto;
import org.springframework.stereotype.Component;

@Component
public class CheckoutResultDtoToCheckoutResultMapper implements BaseMapper<CheckoutResultDto, CheckoutResult> {

    @Override
    public CheckoutResult map(CheckoutResultDto source) {
        return new CheckoutResult()
                .orderNumber(source.getOrderNumber())
                .itemCount(source.getItemCount())
                .totalPrice(source.getTotalPrice());
    }
}
