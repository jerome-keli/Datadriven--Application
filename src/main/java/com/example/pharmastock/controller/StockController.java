package com.example.pharmastock.controller;

import com.example.pharmastock.dto.BulkImportResult;
import com.example.pharmastock.dto.StockAvailability;
import com.example.pharmastock.model.Stock;
import com.example.pharmastock.model.User;
import com.example.pharmastock.security.CurrentUserResolver;
import com.example.pharmastock.service.StockService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/stock")
public class StockController {

    private final StockService stockService;
    private final CurrentUserResolver currentUserResolver;

    public StockController(StockService stockService, CurrentUserResolver currentUserResolver) {
        this.stockService = stockService;
        this.currentUserResolver = currentUserResolver;
    }

    /** Public: "which pharmacies have drug X in stock?" - the core stock-search feature. */
    @GetMapping("/availability/{drugId}")
    public List<StockAvailability> availability(@PathVariable String drugId) {
        return stockService.findAvailability(drugId);
    }

    @GetMapping("/pharmacy/{pharmacyId}")
    public List<Stock> byPharmacy(@PathVariable String pharmacyId) {
        return stockService.findByPharmacy(pharmacyId);
    }

    /** Pharmacist-only: create or update a stock entry for their own pharmacy. */
    @PostMapping
    public ResponseEntity<Stock> upsert(@RequestBody Stock stock, HttpServletRequest request) {
        User pharmacist = currentUserResolver.resolve(request);
        return ResponseEntity.ok(stockService.upsert(pharmacist, stock));
    }

    @DeleteMapping("/{stockId}")
    public ResponseEntity<Void> delete(@PathVariable String stockId, HttpServletRequest request) {
        User pharmacist = currentUserResolver.resolve(request);
        stockService.delete(pharmacist, stockId);
        return ResponseEntity.noContent().build();
    }

    /**
     * Pharmacist-only: upload a CSV to set stock for many drugs at once instead of
     * one API call per drug. Expected columns: drugId,quantity,price,batchNumber,expiryDate
     */
    @PostMapping("/bulk-import")
    public BulkImportResult bulkImport(@RequestParam("file") MultipartFile file, HttpServletRequest request) {
        User pharmacist = currentUserResolver.resolve(request);
        return stockService.bulkImport(pharmacist, file);
    }
}
