package com.example.pharmastock.security;

import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Simple in-memory session store: token -> userId.
 * Adequate for an MVP/single-instance deployment. A production version would
 * move this to Redis or a signed JWT so sessions survive a restart / scale
 * across multiple instances.
 */
@Component
public class TokenStore {

    private final Map<String, String> tokenToUserId = new ConcurrentHashMap<>();

    public String issueToken(String userId) {
        String token = UUID.randomUUID().toString();
        tokenToUserId.put(token, userId);
        return token;
    }

    public String resolve(String token) {
        return tokenToUserId.get(token);
    }

    public void revoke(String token) {
        tokenToUserId.remove(token);
    }
}
