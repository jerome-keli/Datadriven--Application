package com.example.pharmastock.notification;

import com.example.pharmastock.model.User;
import org.springframework.stereotype.Component;

/**
 * Simulated SMS channel. Same AbstractNotifier base as EmailNotifier, but a
 * completely different deliver() - this is the polymorphism: NotificationService
 * calls send() on a List<Notifier> without knowing or caring which concrete
 * channel it's talking to.
 */
@Component
public class SmsNotifier extends AbstractNotifier {

    @Override
    protected void deliver(User recipient, String formattedMessage) {
        System.out.println("[SMS -> " + recipient.getPhone() + "] " + formattedMessage);
    }
}
