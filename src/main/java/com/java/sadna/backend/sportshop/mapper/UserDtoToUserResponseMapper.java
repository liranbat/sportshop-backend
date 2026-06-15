package com.java.sadna.backend.sportshop.mapper;

import com.java.sadna.backend.sportshop.api.generated.authusers.model.UserResponse;
import com.java.sadna.backend.sportshop.model.UserDto;
import org.springframework.stereotype.Component;

@Component
public class UserDtoToUserResponseMapper implements BaseMapper<UserDto, UserResponse> {

    @Override
    public UserResponse map(UserDto user) {
        return new UserResponse(
                user.getId(),
                user.getFirstName(),
                user.getLastName(),
                user.getEmail(),
                user.getPhone(),
                user.isAdmin(),
                user.isDeleted()
        );
    }
}
