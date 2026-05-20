package com.java.sadna.backend.sportshop.mapper;

import com.java.sadna.backend.sportshop.api.generated.authusers.model.UserResponseDto;
import com.java.sadna.backend.sportshop.model.User;
import org.springframework.stereotype.Component;

@Component
public class UserToUserResponseDtoMapper {

    public UserResponseDto map(User user) {
        return new UserResponseDto(
                user.getId(),
                user.getFirstName(),
                user.getLastName(),
                user.getEmail(),
                user.getPhone(),
                user.isAdmin()
        );
    }
}
