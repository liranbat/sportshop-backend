package com.java.sadna.backend.sportshop.mapper;

import com.java.sadna.backend.sportshop.api.generated.orders.model.OrderShipping;
import com.java.sadna.backend.sportshop.model.ShippingDetailsDto;
import org.springframework.stereotype.Component;

@Component
public class OrderShippingToShippingDetailsDtoMapper implements BaseMapper<OrderShipping, ShippingDetailsDto> {

    @Override
    public ShippingDetailsDto map(OrderShipping source) {
        return new ShippingDetailsDto(
                source.getFullName(),
                source.getEmail(),
                source.getPhone(),
                source.getCountry(),
                source.getCity(),
                source.getAddressLine()
        );
    }
}
