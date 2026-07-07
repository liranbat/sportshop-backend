package com.java.sadna.backend.sportshop.mapper.dto.response;

import com.java.sadna.backend.sportshop.api.generated.authusers.model.Session;
import com.java.sadna.backend.sportshop.mapper.BaseMapper;
import com.java.sadna.backend.sportshop.model.SessionDto;
import org.springframework.stereotype.Component;

@Component
public class SessionDtoToSessionMapper implements BaseMapper<SessionDto, Session> {

    @Override
    public Session map(SessionDto source) {
        return new Session(
                source.getId(),
                source.getUserId(),
                source.getFirstName(),
                source.getLastName(),
                source.getEmail(),
                source.getExpiresAt()
        );
    }
}
