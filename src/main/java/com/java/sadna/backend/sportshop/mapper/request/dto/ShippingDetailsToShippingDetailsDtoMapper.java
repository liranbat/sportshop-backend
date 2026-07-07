package com.java.sadna.backend.sportshop.mapper.request.dto;

import com.java.sadna.backend.sportshop.api.generated.checkout.model.ShippingDetails;
import com.java.sadna.backend.sportshop.mapper.BaseMapper;
import com.java.sadna.backend.sportshop.model.ShippingDetailsDto;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface ShippingDetailsToShippingDetailsDtoMapper extends BaseMapper<ShippingDetails, ShippingDetailsDto> {

    @Override
    ShippingDetailsDto map(ShippingDetails source);
}
