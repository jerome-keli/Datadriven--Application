package com.example.pharmastock.controller;

import com.example.pharmastock.dto.PaymentRequest;
import com.example.pharmastock.dto.ReservationRequest;
import com.example.pharmastock.model.Payment;
import com.example.pharmastock.model.Reservation;
import com.example.pharmastock.model.User;
import com.example.pharmastock.security.CurrentUserResolver;
import com.example.pharmastock.service.ReservationService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/reservations")
public class ReservationController {

    private final ReservationService reservationService;
    private final CurrentUserResolver currentUserResolver;

    public ReservationController(ReservationService reservationService, CurrentUserResolver currentUserResolver) {
        this.reservationService = reservationService;
        this.currentUserResolver = currentUserResolver;
    }

    /** Customer places a reservation. Requires an Authorization: Bearer <token> header. */
    @PostMapping
    public ResponseEntity<Reservation> create(@Valid @RequestBody ReservationRequest req, HttpServletRequest request) {
        User customer = currentUserResolver.resolve(request);
        List<Reservation.Item> items = req.getItems().stream()
                .map(i -> new Reservation.Item(i.getDrugId(), i.getName(), i.getQty(), 0))
                .toList();
        Reservation reservation = reservationService.create(customer, req.getPharmacyId(), items);
        return ResponseEntity.status(HttpStatus.CREATED).body(reservation);
    }

    @GetMapping("/mine")
    public List<Reservation> myReservations(HttpServletRequest request) {
        User customer = currentUserResolver.resolve(request);
        return reservationService.findByCustomer(customer.getId());
    }

    @GetMapping("/pharmacy/{pharmacyId}")
    public List<Reservation> byPharmacy(@PathVariable String pharmacyId) {
        return reservationService.findByPharmacy(pharmacyId);
    }

    /** Pharmacist advances status, e.g. PATCH /api/reservations/{id}/status?status=CONFIRMED */
    @PatchMapping("/{id}/status")
    public Reservation updateStatus(@PathVariable String id, @RequestParam Reservation.Status status,
                                     HttpServletRequest request) {
        User pharmacist = currentUserResolver.resolve(request);
        return reservationService.updateStatus(pharmacist, id, status);
    }

    /** Customer pays for a PENDING reservation - writes to MySQL, then confirms in MongoDB. */
    @PostMapping("/{id}/pay")
    public Reservation pay(@PathVariable String id, @Valid @RequestBody PaymentRequest req, HttpServletRequest request) {
        User customer = currentUserResolver.resolve(request);
        Payment.Method method = Payment.Method.valueOf(req.getMethod().toUpperCase());
        return reservationService.payAndConfirm(customer, id, method);
    }

    @PostMapping("/{id}/cancel")
    public Reservation cancel(@PathVariable String id, HttpServletRequest request) {
        User customer = currentUserResolver.resolve(request);
        return reservationService.cancel(customer, id);
    }
}
