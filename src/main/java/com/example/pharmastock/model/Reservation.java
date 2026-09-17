package com.example.pharmastock.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;
import java.util.List;

@Document(collection = "reservations")
public class Reservation {

    @Id
    private String id;

    private String customerId;
    private String pharmacyId;
    private List<Item> items;
    private Status status = Status.PENDING;
    private double totalAmount;
    private Instant createdAt = Instant.now();
    private Instant updatedAt = Instant.now();

    public enum Status { PENDING, CONFIRMED, READY_FOR_PICKUP, COMPLETED, CANCELLED }

    public static class Item {
        private String drugId;
        private String name;
        private int qty;
        private double priceAtReservation;

        public Item() {}

        public Item(String drugId, String name, int qty, double priceAtReservation) {
            this.drugId = drugId;
            this.name = name;
            this.qty = qty;
            this.priceAtReservation = priceAtReservation;
        }

        public String getDrugId() { return drugId; }
        public void setDrugId(String drugId) { this.drugId = drugId; }
        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
        public int getQty() { return qty; }
        public void setQty(int qty) { this.qty = qty; }
        public double getPriceAtReservation() { return priceAtReservation; }
        public void setPriceAtReservation(double priceAtReservation) { this.priceAtReservation = priceAtReservation; }
    }

    public Reservation() {}

    public Reservation(String customerId, String pharmacyId, List<Item> items, double totalAmount) {
        this.customerId = customerId;
        this.pharmacyId = pharmacyId;
        this.items = items;
        this.totalAmount = totalAmount;
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getCustomerId() { return customerId; }
    public void setCustomerId(String customerId) { this.customerId = customerId; }
    public String getPharmacyId() { return pharmacyId; }
    public void setPharmacyId(String pharmacyId) { this.pharmacyId = pharmacyId; }
    public List<Item> getItems() { return items; }
    public void setItems(List<Item> items) { this.items = items; }
    public Status getStatus() { return status; }
    public void setStatus(Status status) { this.status = status; }
    public double getTotalAmount() { return totalAmount; }
    public void setTotalAmount(double totalAmount) { this.totalAmount = totalAmount; }
    public Instant getCreatedAt() { return createdAt; }
    public void setCreatedAt(Instant createdAt) { this.createdAt = createdAt; }
    public Instant getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(Instant updatedAt) { this.updatedAt = updatedAt; }
}
