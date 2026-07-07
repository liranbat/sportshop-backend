package com.java.sadna.backend.sportshop.mapper.entity.dto;

import com.java.sadna.backend.sportshop.entity.UserEntity;
import com.java.sadna.backend.sportshop.mapper.BaseMapper;
import com.java.sadna.backend.sportshop.model.CustomerForOrderDto;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface UserEntityToCustomerForOrderDtoMapper extends BaseMapper<UserEntity, CustomerForOrderDto> {

    @Override
    CustomerForOrderDto map(UserEntity user);
}
