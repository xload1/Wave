package com.example.wave.services;

import com.example.wave.entities.UserAccount;
import com.example.wave.repositories.UserAccountRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AuthService {

    private final UserAccountRepository userAccountRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;

    @Transactional
    public UserAccount register(String displayName, String email, String rawPassword) {
        validateRegistrationInput(displayName, email, rawPassword);

        String normalizedDisplayName = displayName.trim();
        String normalizedEmail = email.trim().toLowerCase();

        if (userAccountRepository.existsByEmail(normalizedEmail)) {
            throw new IllegalArgumentException("Email already exists: " + normalizedEmail);
        }

        String passwordHash = passwordEncoder.encode(rawPassword);
        UserAccount user = new UserAccount(normalizedDisplayName, normalizedEmail, passwordHash);

        return userAccountRepository.save(user);
    }

    public UserAccount login(String email, String rawPassword, HttpServletRequest request) {
        if (email == null || email.isBlank()) {
            throw new IllegalArgumentException("email must not be blank");
        }
        if (rawPassword == null || rawPassword.isBlank()) {
            throw new IllegalArgumentException("password must not be blank");
        }

        String normalizedEmail = email.trim().toLowerCase();

        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(normalizedEmail, rawPassword)
        );

        SecurityContext context = SecurityContextHolder.createEmptyContext();
        context.setAuthentication(authentication);
        SecurityContextHolder.setContext(context);

        HttpSession session = request.getSession(true);
        session.setAttribute(HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY, context);

        return userAccountRepository.findByEmail(normalizedEmail)
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + normalizedEmail));
    }

    @Transactional
    public void changePassword(Long userId, String currentRawPassword, String newRawPassword) {
        if (userId == null) {
            throw new IllegalArgumentException("userId must not be null");
        }
        if (currentRawPassword == null || currentRawPassword.isBlank()) {
            throw new IllegalArgumentException("current password must not be blank");
        }
        if (newRawPassword == null || newRawPassword.isBlank()) {
            throw new IllegalArgumentException("new password must not be blank");
        }
        if (newRawPassword.length() < 6) {
            throw new IllegalArgumentException("new password must be at least 6 characters long");
        }

        UserAccount user = userAccountRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User not found: " + userId));

        if (!passwordEncoder.matches(currentRawPassword, user.getPasswordHash())) {
            throw new IllegalArgumentException("Invalid current password");
        }

        user.changePasswordHash(passwordEncoder.encode(newRawPassword));
    }

    public UserAccount getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated() || "anonymousUser".equals(authentication.getPrincipal())) {
            throw new IllegalStateException("No authenticated user");
        }

        String email = authentication.getName();

        return userAccountRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + email));
    }

    private void validateRegistrationInput(String displayName, String email, String rawPassword) {
        if (displayName == null || displayName.isBlank()) {
            throw new IllegalArgumentException("displayName must not be blank");
        }
        if (email == null || email.isBlank()) {
            throw new IllegalArgumentException("email must not be blank");
        }
        if (rawPassword == null || rawPassword.isBlank()) {
            throw new IllegalArgumentException("password must not be blank");
        }
        if (rawPassword.length() < 6) {
            throw new IllegalArgumentException("password must be at least 6 characters long");
        }
    }
}
