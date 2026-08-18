package com.java.sadna.backend.sportshop.mapper.dto.response;

import com.java.sadna.backend.sportshop.api.generated.sales.model.SalesSummary;
import com.java.sadna.backend.sportshop.mapper.BaseMapper;
import com.java.sadna.backend.sportshop.model.SalesSummaryDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

@Mapper(componentModel = "spring",
        uses = {
                SalesPeriodStatsDtoToSalesPeriodStatsMapper.class,
                SalesStatusStatsDtoToSalesStatusStatsMapper.class,
                SalesTopProductStatsDtoToSalesTopProductStatsMapper.class
        })
public interface SalesSummaryDtoToSalesSummaryMapper extends BaseMapper<SalesSummaryDto, SalesSummary> {

    @Override
    @Mapping(target = "topProductsSortBy", qualifiedByName = "toTopProductsSortByEnum")
    SalesSummary map(SalesSummaryDto source);

    // The wire values are lowercase, so the default Enum.valueOf(name) mapping would blow up.
    @Named("toTopProductsSortByEnum")
    default SalesSummary.TopProductsSortByEnum toTopProductsSortByEnum(String sortBy) {
        return sortBy == null ? null : SalesSummary.TopProductsSortByEnum.fromValue(sortBy);
    }
}
