package com.example.pharmastock.dto;

/** What a customer actually wants from "find this drug near me": pharmacy + price + qty. */
public class StockAvailability {
    private String pharmacyId;
    private String pharmacyName;
    private String pharmacyCity;
    private String pharmacyAddress;
    private double price;
    private int quantity;

    public StockAvailability(String pharmacyId, String pharmacyName, String pharmacyCity,
                              String pharmacyAddress, double price, int quantity) {
        this.pharmacyId = pharmacyId;
        this.pharmacyName = pharmacyName;
        this.pharmacyCity = pharmacyCity;
        this.pharmacyAddress = pharmacyAddress;
        this.price = price;
        this.quantity = quantity;
    }

    public String getPharmacyId() { return pharmacyId; }
    public String getPharmacyName() { return pharmacyName; }
    public String getPharmacyCity() { return pharmacyCity; }
    public String getPharmacyAddress() { return pharmacyAddress; }
    public double getPrice() { return price; }
    public int getQuantity() { return quantity; }
}
