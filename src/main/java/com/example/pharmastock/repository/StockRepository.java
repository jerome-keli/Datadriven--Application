package com.example.pharmastock.repository;

import com.example.pharmastock.model.Stock;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;
import java.util.Optional;

public interface StockRepository extends MongoRepository<Stock, String> {
    List<Stock> findByDrugIdAndQuantityGreaterThan(String drugId, int minQty);
    List<Stock> findByPharmacyId(String pharmacyId);
    Optional<Stock> findByPharmacyIdAndDrugId(String pharmacyId, String drugId);
}
