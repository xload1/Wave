package com.example.wave.controllers;

import com.example.wave.DTOs.requests.ChangePasswordRequest;
import com.example.wave.DTOs.requests.LoginRequest;
import com.example.wave.DTOs.requests.RegisterRequest;
import com.example.wave.DTOs.views.AuthUserView;
import com.example.wave.entities.UserAccount;
import com.example.wave.services.AuthService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
@Validated
public class AuthController {

    private final AuthService authService;

    @PostMapping("/register")
    public ResponseEntity<AuthUserView> register(@Valid @RequestBody RegisterRequest request) {
        UserAccount user = authService.register(
                request.displayName(),
                request.email(),
                request.password()
        );

        return ResponseEntity.status(HttpStatus.CREATED).body(toView(user));
    }

    @PostMapping("/login")
    public AuthUserView login(@Valid @RequestBody LoginRequest request, HttpServletRequest httpRequest) {
        UserAccount user = authService.login(request.email(), request.password(), httpRequest);
        return toView(user);
    }

    @GetMapping("/me")
    public AuthUserView me() {
        UserAccount user = authService.getCurrentUser();
        return toView(user);
    }

    @PostMapping("/change-password")
    public ResponseEntity<Void> changePassword(@Valid @RequestBody ChangePasswordRequest request) {
        UserAccount user = authService.getCurrentUser();
        authService.changePassword(user.getId(), request.currentPassword(), request.newPassword());
        return ResponseEntity.noContent().build();
    }

    private AuthUserView toView(UserAccount user) {
        return new AuthUserView(
                user.getId(),
                user.getDisplayName(),
                user.getEmail(),
                user.getDescription()
        );
    }
}
