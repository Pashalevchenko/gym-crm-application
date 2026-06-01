package com.gym.crm.application.service.common;

import com.gym.crm.application.entity.User;
import com.gym.crm.application.exception.AuthenticationFailedException;
import com.gym.crm.application.repository.UserRepository;
import com.gym.crm.application.security.JwtService;
import lombok.AllArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class AuthenticationService {
    private final UserRepository repository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public String authenticate(String username, String password) {

        User user = repository.findByUsername(username)
                .orElseThrow(() -> new AuthenticationFailedException("Invalid username or password"));

        verifyPassword(password, user.getPassword(), "Invalid username or password");

        return jwtService.generateToken(user.getUsername());
    }

    public void verifyPassword(String rawPassword, String encodedPassword, String errorMessage) {
        if (!passwordEncoder.matches(rawPassword, encodedPassword)) {
            throw new AuthenticationFailedException(errorMessage);
        }
    }

    public String encodePassword(String rawPassword) {
        return passwordEncoder.encode(rawPassword);
    }
}
