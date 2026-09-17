package com.example.pharmastock.repository;

import com.example.pharmastock.model.Payment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface PaymentRepository extends JpaRepository<Payment, Long> {
    Optional<Payment> findByReservationId(String reservationId);
    List<Payment> findByCustomerId(String customerId);
    List<Payment> findByPharmacyId(String pharmacyId);
}
