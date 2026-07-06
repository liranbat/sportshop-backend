package com.java.sadna.backend.sportshop.model;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class ShippingDetailsDto {

    private final String fullName;
    private final String email;
    private final String phone;
    private final String country;
    private final String city;
    private final String addressLine;
}
