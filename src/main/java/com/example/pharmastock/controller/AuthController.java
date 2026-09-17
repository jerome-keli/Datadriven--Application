package com.example.pharmastock.controller;

import com.example.pharmastock.dto.AuthDtos.LoginRequest;
import com.example.pharmastock.dto.AuthDtos.LoginResponse;
import com.example.pharmastock.dto.AuthDtos.RegisterRequest;
import com.example.pharmastock.model.User;
import com.example.pharmastock.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/register")
    public ResponseEntity<User> register(@Valid @RequestBody RegisterRequest req) {
        User.Role role = User.Role.valueOf(req.getRole().toUpperCase());
        User user = authService.register(req.getName(), req.getEmail(), req.getPhone(),
                req.getPassword(), role, req.getPharmacyId());
        user.setPasswordHash(null); // never echo the hash back
        return ResponseEntity.status(HttpStatus.CREATED).body(user);
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@Valid @RequestBody LoginRequest req) {
        User user = authService.authenticate(req.getEmail(), req.getPassword());
        String token = authService.issueToken(user.getId());
        return ResponseEntity.ok(new LoginResponse(token, user.getId(), user.getRole()));
    }
}
