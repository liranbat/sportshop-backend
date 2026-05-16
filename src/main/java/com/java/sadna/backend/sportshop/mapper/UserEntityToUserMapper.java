package com.java.sadna.backend.sportshop.mapper;

import com.java.sadna.backend.sportshop.entity.UserEntity;
import com.java.sadna.backend.sportshop.model.User;
import org.springframework.stereotype.Component;

@Component
public class UserEntityToUserMapper {

    public User map(UserEntity userEntity) {
        return new User(
                userEntity.getId(),
                userEntity.getFirstName(),
                userEntity.getLastName(),
                userEntity.getEmail(),
                userEntity.getPhone(),
                userEntity.isAdmin()
        );
    }
}
