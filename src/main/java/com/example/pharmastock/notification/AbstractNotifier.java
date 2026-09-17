package com.example.pharmastock.notification;

import com.example.pharmastock.model.User;

import java.time.Instant;

/**
 * Template-method base class: every channel formats a message the same way,
 * but delivers it differently. Subclasses only implement deliver().
 */
public abstract class AbstractNotifier implements Notifier {

    @Override
    public final void send(User recipient, String subject, String message) {
        String formatted = format(recipient, subject, message);
        deliver(recipient, formatted);
    }

    protected String format(User recipient, String subject, String message) {
        return "[" + Instant.now() + "] " + subject + " - " + message;
    }

    /** Each concrete channel decides how the formatted message actually gets to the user. */
    protected abstract void deliver(User recipient, String formattedMessage);
}
