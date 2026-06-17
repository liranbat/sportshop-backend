package com.java.sadna.backend.sportshop.mapper;

import com.java.sadna.backend.sportshop.api.generated.orders.model.CustomerForOrder;
import com.java.sadna.backend.sportshop.model.CustomerForOrderDto;
import org.springframework.stereotype.Component;

@Component
public class CustomerForOrderDtoToCustomerForOrderMapper implements BaseMapper<CustomerForOrderDto, CustomerForOrder> {

    @Override
    public CustomerForOrder map(CustomerForOrderDto source) {
        return new CustomerForOrder(
                source.getId(),
                source.getFirstName(),
                source.getLastName(),
                source.getEmail()
        );
    }
}
