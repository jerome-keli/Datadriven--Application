package com.example.pharmastock.repository;

import com.example.pharmastock.model.Reservation;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface ReservationRepository extends MongoRepository<Reservation, String> {
    List<Reservation> findByCustomerId(String customerId);
    List<Reservation> findByPharmacyId(String pharmacyId);
    List<Reservation> findByPharmacyIdAndStatus(String pharmacyId, Reservation.Status status);
}
