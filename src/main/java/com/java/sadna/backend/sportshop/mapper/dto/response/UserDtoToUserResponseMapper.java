package com.java.sadna.backend.sportshop.mapper.dto.response;

import com.java.sadna.backend.sportshop.api.generated.authusers.model.UserResponse;
import com.java.sadna.backend.sportshop.mapper.BaseMapper;
import com.java.sadna.backend.sportshop.model.UserDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface UserDtoToUserResponseMapper extends BaseMapper<UserDto, UserResponse> {

    @Override
    @Mapping(target = "isAdmin", source = "admin")
    @Mapping(target = "isDeleted", source = "deleted")
    UserResponse map(UserDto source);
}
