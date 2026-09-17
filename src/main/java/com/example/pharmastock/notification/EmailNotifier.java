package com.example.pharmastock.notification;

import com.example.pharmastock.model.User;
import org.springframework.stereotype.Component;

/**
 * Simulated email channel: logs what would be sent rather than calling a real
 * SMTP provider. Swap the body of deliver() for a real mail client (e.g.
 * JavaMailSender) without touching anything else - that's the point of
 * depending on the Notifier interface rather than a concrete class.
 */
@Component
public class EmailNotifier extends AbstractNotifier {

    @Override
    protected void deliver(User recipient, String formattedMessage) {
        System.out.println("[EMAIL -> " + recipient.getEmail() + "] " + formattedMessage);
    }
}
