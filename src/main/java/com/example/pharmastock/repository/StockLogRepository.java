package com.example.pharmastock.repository;

import com.example.pharmastock.model.StockLog;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface StockLogRepository extends MongoRepository<StockLog, String> {
    List<StockLog> findByPharmacyIdOrderByTimestampDesc(String pharmacyId);
}
