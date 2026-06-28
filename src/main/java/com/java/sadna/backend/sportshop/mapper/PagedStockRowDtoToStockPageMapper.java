package com.java.sadna.backend.sportshop.mapper;

import com.java.sadna.backend.sportshop.api.generated.stock.model.StockPage;
import com.java.sadna.backend.sportshop.api.generated.stock.model.StockRow;
import com.java.sadna.backend.sportshop.model.PagedResult;
import com.java.sadna.backend.sportshop.model.StockRowDto;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class PagedStockRowDtoToStockPageMapper
        implements BaseMapper<PagedResult<StockRowDto>, StockPage> {

    private final StockRowDtoToStockRowMapper stockRowDtoToStockRowMapper;

    public PagedStockRowDtoToStockPageMapper(StockRowDtoToStockRowMapper stockRowDtoToStockRowMapper) {
        this.stockRowDtoToStockRowMapper = stockRowDtoToStockRowMapper;
    }

    @Override
    public StockPage map(PagedResult<StockRowDto> source) {
        List<StockRow> items = source.getItems().stream()
                .map(stockRowDtoToStockRowMapper::map)
                .toList();
        return new StockPage(
                items,
                source.getPage(),
                source.getPageSize(),
                source.getTotalElements(),
                source.getTotalPages()
        );
    }
}
