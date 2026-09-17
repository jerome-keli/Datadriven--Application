package com.example.pharmastock.repository;

import com.example.pharmastock.model.Pharmacy;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface PharmacyRepository extends MongoRepository<Pharmacy, String> {
    List<Pharmacy> findByLocationCityIgnoreCase(String city);
}
