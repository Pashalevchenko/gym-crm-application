package com.gym.crm.application.service.impl;

import com.gym.crm.application.dao.UserDao;
import com.gym.crm.application.entity.User;
import com.gym.crm.application.openapi.LoginChangeRequest;
import com.gym.crm.application.service.UserService;
import com.gym.crm.application.validation.TrainingValidator;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.NoSuchElementException;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserDao userDao;
    private final TrainingValidator validator;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void changePassword(LoginChangeRequest request) {
        User existingUser = userDao.findByUsername(request.getUsername())
                .orElseThrow(() -> new EntityNotFoundException("User not found"));

        if (!passwordEncoder.matches(request.getOldPassword(), existingUser.getPassword())) {
            throw new IllegalArgumentException("Invalid password");
        }

        User userToUpdate = existingUser.toBuilder()
                .password(passwordEncoder.encode(request.getNewPassword()))
                .build();

        userDao.update(userToUpdate);
        log.info("Password changed for trainee username: {}", request.getUsername());
    }

    @Override
    public User findByUsername(String username) {
        return userDao.findByUsername(username)
                .orElseThrow(() -> new NoSuchElementException("User not found"));
    }
}
