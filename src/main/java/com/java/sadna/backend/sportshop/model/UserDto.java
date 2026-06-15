package com.java.sadna.backend.sportshop.model;

public class UserDto implements BaseDto {

    private final Long id;
    private final String firstName;
    private final String lastName;
    private final String email;
    private final String phone;
    private final boolean admin;
    private final boolean deleted;

    public UserDto(Long id, String firstName, String lastName, String email, String phone, boolean admin, boolean deleted) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.phone = phone;
        this.admin = admin;
        this.deleted = deleted;
    }

    public Long getId() {
        return id;
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

    public String getPhone() {
        return phone;
    }

    public boolean isAdmin() {
        return admin;
    }

    public boolean isDeleted() {
        return deleted;
    }
}
