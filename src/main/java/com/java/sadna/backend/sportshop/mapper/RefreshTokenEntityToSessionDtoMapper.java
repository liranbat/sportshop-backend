package com.java.sadna.backend.sportshop.mapper;

import com.java.sadna.backend.sportshop.entity.RefreshTokenEntity;
import com.java.sadna.backend.sportshop.entity.UserEntity;
import com.java.sadna.backend.sportshop.model.SessionDto;
import org.springframework.stereotype.Component;

@Component
public class RefreshTokenEntityToSessionDtoMapper implements BaseMapper<RefreshTokenEntity, SessionDto> {

    @Override
    public SessionDto map(RefreshTokenEntity rt) {
        UserEntity user = rt.getUser();
        return new SessionDto(
                rt.getId(),
                rt.getUserId(),
                user != null ? user.getFirstName() : null,
                user != null ? user.getLastName() : null,
                user != null ? user.getEmail() : null,
                rt.getExpiresAt()
        );
    }
}
