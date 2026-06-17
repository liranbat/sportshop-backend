package com.java.sadna.backend.sportshop.mapper;

import com.java.sadna.backend.sportshop.entity.UserEntity;
import com.java.sadna.backend.sportshop.model.CustomerForOrderDto;
import org.springframework.stereotype.Component;

@Component
public class UserEntityToCustomerForOrderDtoMapper implements BaseMapper<UserEntity, CustomerForOrderDto> {

    @Override
    public CustomerForOrderDto map(UserEntity user) {
        return new CustomerForOrderDto(
                user.getId(),
                user.getFirstName(),
                user.getLastName(),
                user.getEmail()
        );
    }
}
