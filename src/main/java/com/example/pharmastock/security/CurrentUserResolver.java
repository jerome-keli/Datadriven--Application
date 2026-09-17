package com.example.pharmastock.security;

import com.example.pharmastock.exception.AuthException;
import com.example.pharmastock.model.User;
import com.example.pharmastock.repository.UserRepository;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Component;

@Component
public class CurrentUserResolver {

    private final TokenStore tokenStore;
    private final UserRepository userRepository;

    public CurrentUserResolver(TokenStore tokenStore, UserRepository userRepository) {
        this.tokenStore = tokenStore;
        this.userRepository = userRepository;
    }

    /** Reads "Authorization: Bearer <token>", resolves to a User, or throws AuthException (401). */
    public User resolve(HttpServletRequest request) {
        String header = request.getHeader("Authorization");
        if (header == null || !header.startsWith("Bearer ")) {
            throw new AuthException("Missing or malformed Authorization header");
        }
        String token = header.substring("Bearer ".length()).trim();
        String userId = tokenStore.resolve(token);
        if (userId == null) {
            throw new AuthException("Invalid or expired session token");
        }
        return userRepository.findById(userId)
                .orElseThrow(() -> new AuthException("Session refers to a user that no longer exists"));
    }
}
