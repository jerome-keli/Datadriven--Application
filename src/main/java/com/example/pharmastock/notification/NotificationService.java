package com.example.pharmastock.notification;

import com.example.pharmastock.model.Reservation;
import com.example.pharmastock.model.User;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Spring injects every Notifier bean into this list automatically (EmailNotifier,
 * SmsNotifier, and any future channel). notifyReservationUpdate() runs on the
 * "notificationExecutor" pool (see AsyncConfig) so the caller (ReservationService)
 * returns to the customer immediately instead of waiting on notification delivery.
 */
@Service
public class NotificationService {

    private final List<Notifier> notifiers;

    public NotificationService(List<Notifier> notifiers) {
        this.notifiers = notifiers;
    }

    @Async("notificationExecutor")
    public void notifyReservationUpdate(User customer, Reservation reservation, String event) {
        String subject = "Reservation " + reservation.getId() + " - " + event;
        String message = "Your reservation is now " + reservation.getStatus()
                + " (total: " + reservation.getTotalAmount() + ")";
        for (Notifier notifier : notifiers) {
            notifier.send(customer, subject, message);
        }
    }
}
