package com.java.sadna.backend.sportshop.model;

public class ShippingDetailsDto {

    private final String fullName;
    private final String email;
    private final String phone;
    private final String country;
    private final String city;
    private final String addressLine;

    public ShippingDetailsDto(String fullName,
                              String email,
                              String phone,
                              String country,
                              String city,
                              String addressLine) {
        this.fullName = fullName;
        this.email = email;
        this.phone = phone;
        this.country = country;
        this.city = city;
        this.addressLine = addressLine;
    }

    public String getFullName() {
        return fullName;
    }

    public String getEmail() {
        return email;
    }

    public String getPhone() {
        return phone;
    }

    public String getCountry() {
        return country;
    }

    public String getCity() {
        return city;
    }

    public String getAddressLine() {
        return addressLine;
    }
}
