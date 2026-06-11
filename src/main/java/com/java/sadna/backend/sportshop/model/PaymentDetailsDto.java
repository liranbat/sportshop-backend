package com.java.sadna.backend.sportshop.model;

public class PaymentDetailsDto {

    private final String cardNumber;
    private final String expiry;
    private final String cvv;
    private final String nameOnCard;

    public PaymentDetailsDto(String cardNumber, String expiry, String cvv, String nameOnCard) {
        this.cardNumber = cardNumber;
        this.expiry = expiry;
        this.cvv = cvv;
        this.nameOnCard = nameOnCard;
    }

    public String getCardNumber() {
        return cardNumber;
    }

    public String getExpiry() {
        return expiry;
    }

    public String getCvv() {
        return cvv;
    }

    public String getNameOnCard() {
        return nameOnCard;
    }
}
