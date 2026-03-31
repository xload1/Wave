package com.example.wave.services;

import com.example.wave.DTOs.requests.CreateUserRequest;
import com.example.wave.DTOs.responses.CreateUserResponse;
import com.example.wave.entities.UserAccount;
import com.example.wave.repositories.UserAccountRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserService {

    private final UserAccountRepository userAccountRepository;

    @Transactional
    public CreateUserResponse createUser(CreateUserRequest request) {
        String username = request.username().trim();
        String email = request.email().trim();

        if (userAccountRepository.existsByUsername(username)) {
            throw new IllegalArgumentException("Username already exists: " + username);
        }

        if (userAccountRepository.existsByEmail(email)) {
            throw new IllegalArgumentException("Email already exists: " + email);
        }

        UserAccount user = new UserAccount(username, email);
        UserAccount savedUser = userAccountRepository.save(user);

        return new CreateUserResponse(
                savedUser.getId(),
                savedUser.getUsername(),
                savedUser.getEmail()
        );
    }
}