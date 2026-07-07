package com.java.sadna.backend.sportshop.mapper.dto.response;

import com.java.sadna.backend.sportshop.api.generated.orders.model.CustomerForOrder;
import com.java.sadna.backend.sportshop.mapper.BaseMapper;
import com.java.sadna.backend.sportshop.model.CustomerForOrderDto;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface CustomerForOrderDtoToCustomerForOrderMapper extends BaseMapper<CustomerForOrderDto, CustomerForOrder> {

    @Override
    CustomerForOrder map(CustomerForOrderDto source);
}
