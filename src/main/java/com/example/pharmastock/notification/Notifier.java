package com.example.pharmastock.notification;

import com.example.pharmastock.model.User;

/**
 * Contract for any way of reaching a user about a reservation event.
 * Each channel (email, SMS, ...) implements this independently.
 */
public interface Notifier {
    void send(User recipient, String subject, String message);
}
