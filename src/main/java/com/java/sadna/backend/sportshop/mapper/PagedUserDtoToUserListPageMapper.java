package com.java.sadna.backend.sportshop.mapper;

import com.java.sadna.backend.sportshop.api.generated.authusers.model.UserListPage;
import com.java.sadna.backend.sportshop.api.generated.authusers.model.UserResponse;
import com.java.sadna.backend.sportshop.model.PagedResult;
import com.java.sadna.backend.sportshop.model.UserDto;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class PagedUserDtoToUserListPageMapper
        implements BaseMapper<PagedResult<UserDto>, UserListPage> {

    private final UserDtoToUserResponseMapper userDtoToUserResponseMapper;

    public PagedUserDtoToUserListPageMapper(UserDtoToUserResponseMapper userDtoToUserResponseMapper) {
        this.userDtoToUserResponseMapper = userDtoToUserResponseMapper;
    }

    @Override
    public UserListPage map(PagedResult<UserDto> source) {
        List<UserResponse> items = source.getItems().stream()
                .map(userDtoToUserResponseMapper::map)
                .toList();
        return new UserListPage(
                items,
                source.getPage(),
                source.getPageSize(),
                source.getTotalElements(),
                source.getTotalPages()
        );
    }
}
