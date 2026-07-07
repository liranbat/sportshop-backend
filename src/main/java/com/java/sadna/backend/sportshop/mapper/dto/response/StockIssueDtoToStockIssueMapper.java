package com.java.sadna.backend.sportshop.mapper.dto.response;

import com.java.sadna.backend.sportshop.api.generated.cart.model.StockIssue;
import com.java.sadna.backend.sportshop.mapper.BaseMapper;
import com.java.sadna.backend.sportshop.model.StockIssueDto;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface StockIssueDtoToStockIssueMapper extends BaseMapper<StockIssueDto, StockIssue> {

    @Override
    StockIssue map(StockIssueDto source);
}
