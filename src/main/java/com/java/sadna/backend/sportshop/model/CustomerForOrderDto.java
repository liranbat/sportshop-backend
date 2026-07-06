package com.java.sadna.backend.sportshop.model;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class CustomerForOrderDto {

    private final Long id;
    private final String firstName;
    private final String lastName;
    private final String email;
}
