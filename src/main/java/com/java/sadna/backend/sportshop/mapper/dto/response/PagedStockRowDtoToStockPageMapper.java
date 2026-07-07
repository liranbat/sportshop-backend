package com.java.sadna.backend.sportshop.mapper.dto.response;

import com.java.sadna.backend.sportshop.api.generated.stock.model.StockPage;
import com.java.sadna.backend.sportshop.mapper.BaseMapper;
import com.java.sadna.backend.sportshop.model.PagedResult;
import com.java.sadna.backend.sportshop.model.StockRowDto;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring", uses = StockRowDtoToStockRowMapper.class)
public interface PagedStockRowDtoToStockPageMapper
        extends BaseMapper<PagedResult<StockRowDto>, StockPage> {

    @Override
    StockPage map(PagedResult<StockRowDto> source);
}
