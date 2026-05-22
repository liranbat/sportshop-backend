package com.java.sadna.backend.sportshop.mapper;

import com.java.sadna.backend.sportshop.entity.UserEntity;
import com.java.sadna.backend.sportshop.model.UserDto;
import org.springframework.stereotype.Component;

@Component
public class UserEntityToUserDtoMapper implements BaseMapper<UserEntity, UserDto> {

    @Override
    public UserDto map(UserEntity userEntity) {
        return new UserDto(
                userEntity.getId(),
                userEntity.getFirstName(),
                userEntity.getLastName(),
                userEntity.getEmail(),
                userEntity.getPhone(),
                userEntity.isAdmin()
        );
    }
}
