package com.java.sadna.backend.sportshop.mapper.dto.response;

import com.java.sadna.backend.sportshop.api.generated.checkout.model.CheckoutResult;
import com.java.sadna.backend.sportshop.mapper.BaseMapper;
import com.java.sadna.backend.sportshop.model.CheckoutResultDto;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface CheckoutResultDtoToCheckoutResultMapper extends BaseMapper<CheckoutResultDto, CheckoutResult> {

    @Override
    CheckoutResult map(CheckoutResultDto source);
}
