package com.java.sadna.backend.sportshop.model;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.time.OffsetDateTime;

@Getter
@RequiredArgsConstructor
public class SessionDto {

    private final Long id;
    private final Long userId;
    private final String firstName;
    private final String lastName;
    private final String email;
    private final OffsetDateTime expiresAt;
}
