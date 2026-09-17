package com.example.pharmastock.controller;

import com.example.pharmastock.model.Payment;
import com.example.pharmastock.model.User;
import com.example.pharmastock.repository.PaymentRepository;
import com.example.pharmastock.security.CurrentUserResolver;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/payments")
public class PaymentController {

    private final PaymentRepository paymentRepository;
    private final CurrentUserResolver currentUserResolver;

    public PaymentController(PaymentRepository paymentRepository, CurrentUserResolver currentUserResolver) {
        this.paymentRepository = paymentRepository;
        this.currentUserResolver = currentUserResolver;
    }

    @GetMapping("/mine")
    public List<Payment> myPayments(HttpServletRequest request) {
        User customer = currentUserResolver.resolve(request);
        return paymentRepository.findByCustomerId(customer.getId());
    }

    @GetMapping("/pharmacy/{pharmacyId}")
    public List<Payment> byPharmacy(@PathVariable String pharmacyId) {
        return paymentRepository.findByPharmacyId(pharmacyId);
    }
}
