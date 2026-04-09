package com.example.wave.services;

import com.example.wave.DTOs.requests.CreateUserRequest;
import com.example.wave.DTOs.responses.CreateUserResponse;
import com.example.wave.entities.UserAccount;
import com.example.wave.repositories.UserAccountRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.naming.ServiceUnavailableException;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserService {

    private final UserAccountRepository userAccountRepository;

    @Transactional
    public CreateUserResponse createUser(CreateUserRequest request) throws ServiceUnavailableException {
            throw new ServiceUnavailableException("Service deprecated");
    }
}