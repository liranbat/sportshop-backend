package com.java.sadna.backend.sportshop.mapper.entity.dto;

import com.java.sadna.backend.sportshop.entity.RefreshTokenEntity;
import com.java.sadna.backend.sportshop.mapper.BaseMapper;
import com.java.sadna.backend.sportshop.model.SessionDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface RefreshTokenEntityToSessionDtoMapper extends BaseMapper<RefreshTokenEntity, SessionDto> {

    @Override
    @Mapping(source = "user.firstName", target = "firstName")
    @Mapping(source = "user.lastName", target = "lastName")
    @Mapping(source = "user.email", target = "email")
    SessionDto map(RefreshTokenEntity rt);
}
