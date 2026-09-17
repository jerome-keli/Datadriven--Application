package com.example.pharmastock.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;

import java.util.List;

public class ReservationRequest {

    @NotBlank
    private String pharmacyId;

    @NotEmpty
    private List<LineItem> items;

    public static class LineItem {
        @NotBlank private String drugId;
        @NotBlank private String name;
        private int qty;

        public String getDrugId() { return drugId; }
        public void setDrugId(String drugId) { this.drugId = drugId; }
        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
        public int getQty() { return qty; }
        public void setQty(int qty) { this.qty = qty; }
    }

    public String getPharmacyId() { return pharmacyId; }
    public void setPharmacyId(String pharmacyId) { this.pharmacyId = pharmacyId; }
    public List<LineItem> getItems() { return items; }
    public void setItems(List<LineItem> items) { this.items = items; }
}
