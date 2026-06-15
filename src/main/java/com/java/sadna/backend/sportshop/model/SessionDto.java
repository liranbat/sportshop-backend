package com.java.sadna.backend.sportshop.model;

import java.time.OffsetDateTime;

public class SessionDto implements BaseDto {

    private final Long id;
    private final Long userId;
    private final String firstName;
    private final String lastName;
    private final String email;
    private final OffsetDateTime expiresAt;

    public SessionDto(Long id,
                      Long userId,
                      String firstName,
                      String lastName,
                      String email,
                      OffsetDateTime expiresAt) {
        this.id = id;
        this.userId = userId;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.expiresAt = expiresAt;
    }

    public Long getId() {
        return id;
    }

    public Long getUserId() {
        return userId;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public String getEmail() {
        return email;
    }

    public OffsetDateTime getExpiresAt() {
        return expiresAt;
    }
}
