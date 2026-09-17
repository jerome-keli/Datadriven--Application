package com.example.pharmastock.controller;

import com.example.pharmastock.model.Pharmacy;
import com.example.pharmastock.service.PharmacyService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/pharmacies")
public class PharmacyController {

    private final PharmacyService pharmacyService;

    public PharmacyController(PharmacyService pharmacyService) {
        this.pharmacyService = pharmacyService;
    }

    @PostMapping
    public ResponseEntity<Pharmacy> create(@RequestBody Pharmacy pharmacy) {
        return ResponseEntity.status(HttpStatus.CREATED).body(pharmacyService.create(pharmacy));
    }

    @GetMapping
    public List<Pharmacy> findAll(@RequestParam(required = false) String city) {
        return city != null ? pharmacyService.findByCity(city) : pharmacyService.findAll();
    }

    @GetMapping("/{id}")
    public Pharmacy findById(@PathVariable String id) {
        return pharmacyService.findById(id);
    }

    @PutMapping("/{id}")
    public Pharmacy update(@PathVariable String id, @RequestBody Pharmacy pharmacy) {
        return pharmacyService.update(id, pharmacy);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable String id) {
        pharmacyService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
