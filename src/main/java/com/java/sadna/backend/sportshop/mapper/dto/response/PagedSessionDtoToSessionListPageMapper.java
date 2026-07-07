package com.java.sadna.backend.sportshop.mapper.dto.response;

import com.java.sadna.backend.sportshop.api.generated.authusers.model.SessionListPage;
import com.java.sadna.backend.sportshop.mapper.BaseMapper;
import com.java.sadna.backend.sportshop.model.PagedResult;
import com.java.sadna.backend.sportshop.model.SessionDto;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring", uses = SessionDtoToSessionMapper.class)
public interface PagedSessionDtoToSessionListPageMapper
        extends BaseMapper<PagedResult<SessionDto>, SessionListPage> {

    @Override
    SessionListPage map(PagedResult<SessionDto> source);
}
