package com.example.pharmastock.dto;

import com.example.pharmastock.model.User;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public class AuthDtos {

    public static class RegisterRequest {
        @NotBlank private String name;
        @Email @NotBlank private String email;
        @NotBlank private String phone;
        @NotBlank private String password;
        @NotBlank private String role; // CUSTOMER | PHARMACIST | ADMIN
        private String pharmacyId;     // required if role == PHARMACIST

        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
        public String getEmail() { return email; }
        public void setEmail(String email) { this.email = email; }
        public String getPhone() { return phone; }
        public void setPhone(String phone) { this.phone = phone; }
        public String getPassword() { return password; }
        public void setPassword(String password) { this.password = password; }
        public String getRole() { return role; }
        public void setRole(String role) { this.role = role; }
        public String getPharmacyId() { return pharmacyId; }
        public void setPharmacyId(String pharmacyId) { this.pharmacyId = pharmacyId; }
    }

    public static class LoginRequest {
        @Email @NotBlank private String email;
        @NotBlank private String password;

        public String getEmail() { return email; }
        public void setEmail(String email) { this.email = email; }
        public String getPassword() { return password; }
        public void setPassword(String password) { this.password = password; }
    }

    public static class LoginResponse {
        private String token;
        private String userId;
        private User.Role role;

        public LoginResponse(String token, String userId, User.Role role) {
            this.token = token;
            this.userId = userId;
            this.role = role;
        }

        public String getToken() { return token; }
        public String getUserId() { return userId; }
        public User.Role getRole() { return role; }
    }
}
