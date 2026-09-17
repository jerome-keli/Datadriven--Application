package com.example.pharmastock.dto;

import jakarta.validation.constraints.NotBlank;

public class PaymentRequest {
    @NotBlank
    private String method; // CASH | MOBILE_MONEY | CARD

    public String getMethod() { return method; }
    public void setMethod(String method) { this.method = method; }
}
