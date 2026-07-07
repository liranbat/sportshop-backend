package com.java.sadna.backend.sportshop.mapper.dto.response;

import com.java.sadna.backend.sportshop.api.generated.authusers.model.Session;
import com.java.sadna.backend.sportshop.mapper.BaseMapper;
import com.java.sadna.backend.sportshop.model.SessionDto;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface SessionDtoToSessionMapper extends BaseMapper<SessionDto, Session> {

    @Override
    Session map(SessionDto source);
}
