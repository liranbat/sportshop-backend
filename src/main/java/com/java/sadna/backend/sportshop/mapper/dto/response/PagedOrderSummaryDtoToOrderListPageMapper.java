package com.java.sadna.backend.sportshop.mapper.dto.response;

import com.java.sadna.backend.sportshop.api.generated.orders.model.OrderListPage;
import com.java.sadna.backend.sportshop.mapper.BaseMapper;
import com.java.sadna.backend.sportshop.model.OrderSummaryDto;
import com.java.sadna.backend.sportshop.model.PagedResult;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring", uses = OrderSummaryDtoToOrderSummaryMapper.class)
public interface PagedOrderSummaryDtoToOrderListPageMapper
        extends BaseMapper<PagedResult<OrderSummaryDto>, OrderListPage> {

    @Override
    OrderListPage map(PagedResult<OrderSummaryDto> source);
}
