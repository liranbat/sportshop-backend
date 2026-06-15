package com.java.sadna.backend.sportshop.mapper;

import com.java.sadna.backend.sportshop.api.generated.authusers.model.Session;
import com.java.sadna.backend.sportshop.api.generated.authusers.model.SessionListPage;
import com.java.sadna.backend.sportshop.model.PagedResult;
import com.java.sadna.backend.sportshop.model.SessionDto;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class PagedSessionDtoToSessionListPageMapper
        implements BaseMapper<PagedResult<SessionDto>, SessionListPage> {

    @Override
    public SessionListPage map(PagedResult<SessionDto> source) {
        List<Session> items = source.getItems().stream()
                .map(PagedSessionDtoToSessionListPageMapper::toApiSession)
                .toList();
        return new SessionListPage(
                items,
                source.getPage(),
                source.getPageSize(),
                source.getTotalElements(),
                source.getTotalPages()
        );
    }

    private static Session toApiSession(SessionDto dto) {
        return new Session(
                dto.getId(),
                dto.getUserId(),
                dto.getFirstName(),
                dto.getLastName(),
                dto.getEmail(),
                dto.getExpiresAt()
        );
    }
}
