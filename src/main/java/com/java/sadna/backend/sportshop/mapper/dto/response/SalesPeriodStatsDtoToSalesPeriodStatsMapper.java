package com.java.sadna.backend.sportshop.mapper.dto.response;

import com.java.sadna.backend.sportshop.api.generated.sales.model.SalesPeriodStats;
import com.java.sadna.backend.sportshop.mapper.BaseMapper;
import com.java.sadna.backend.sportshop.model.SalesPeriodStatsDto;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface SalesPeriodStatsDtoToSalesPeriodStatsMapper
        extends BaseMapper<SalesPeriodStatsDto, SalesPeriodStats> {

    @Override
    SalesPeriodStats map(SalesPeriodStatsDto source);
}
