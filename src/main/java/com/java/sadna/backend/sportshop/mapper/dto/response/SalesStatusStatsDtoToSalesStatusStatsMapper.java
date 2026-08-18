package com.java.sadna.backend.sportshop.mapper.dto.response;

import com.java.sadna.backend.sportshop.api.generated.sales.model.OrderStatus;
import com.java.sadna.backend.sportshop.api.generated.sales.model.SalesStatusStats;
import com.java.sadna.backend.sportshop.mapper.BaseMapper;
import com.java.sadna.backend.sportshop.model.SalesStatusStatsDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", imports = OrderStatus.class)
public interface SalesStatusStatsDtoToSalesStatusStatsMapper
        extends BaseMapper<SalesStatusStatsDto, SalesStatusStats> {

    @Override
    @Mapping(target = "status", expression = "java(OrderStatus.fromValue(source.getStatus()))")
    SalesStatusStats map(SalesStatusStatsDto source);
}
