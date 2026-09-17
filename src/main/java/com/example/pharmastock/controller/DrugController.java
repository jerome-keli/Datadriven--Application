package com.example.pharmastock.controller;

import com.example.pharmastock.model.Drug;
import com.example.pharmastock.service.DrugService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/drugs")
public class DrugController {

    private final DrugService drugService;

    public DrugController(DrugService drugService) {
        this.drugService = drugService;
    }

    @PostMapping
    public ResponseEntity<Drug> create(@RequestBody Drug drug) {
        return ResponseEntity.status(HttpStatus.CREATED).body(drugService.create(drug));
    }

    @GetMapping
    public List<Drug> findAll(@RequestParam(required = false) String q) {
        return (q != null && !q.isBlank()) ? drugService.search(q) : drugService.findAll();
    }

    @GetMapping("/{id}")
    public Drug findById(@PathVariable String id) {
        return drugService.findById(id);
    }

    @PutMapping("/{id}")
    public Drug update(@PathVariable String id, @RequestBody Drug drug) {
        return drugService.update(id, drug);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable String id) {
        drugService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
