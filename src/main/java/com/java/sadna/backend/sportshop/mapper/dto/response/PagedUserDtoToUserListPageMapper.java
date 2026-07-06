package com.java.sadna.backend.sportshop.mapper.dto.response;

import com.java.sadna.backend.sportshop.api.generated.authusers.model.UserListPage;
import com.java.sadna.backend.sportshop.mapper.BaseMapper;
import com.java.sadna.backend.sportshop.model.PagedResult;
import com.java.sadna.backend.sportshop.model.UserDto;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring", uses = UserDtoToUserResponseMapper.class)
public interface PagedUserDtoToUserListPageMapper
        extends BaseMapper<PagedResult<UserDto>, UserListPage> {

    @Override
    UserListPage map(PagedResult<UserDto> source);
}
