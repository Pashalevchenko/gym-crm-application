package com.gym.crm.application.service.impl;

import com.gym.crm.application.dao.UserDao;
import com.gym.crm.application.entity.User;
import com.gym.crm.application.openapi.LoginChangeRequest;
import com.gym.crm.application.service.UserService;
import com.gym.crm.application.service.common.AuthenticationService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import java.util.NoSuchElementException;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserDao userDao;
    private final AuthenticationService authentication;

    @Override
    public void changePassword(LoginChangeRequest request) {
        User existingUser = userDao.findByUsername(request.getUsername())
                .orElseThrow(() -> new EntityNotFoundException("User not found"));

        authentication.verifyPassword(request.getOldPassword(), existingUser.getPassword(), "The provided old password does not match the current password");

        User userToUpdate = existingUser.toBuilder()
                .password(authentication.encodePassword(request.getNewPassword()))
                .build();

        userDao.update(userToUpdate);
        log.info("Password changed for username: {}", request.getUsername());
    }

    @Override
    public User findByUsername(String username) {
        return userDao.findByUsername(username)
                .orElseThrow(() -> new NoSuchElementException("User not found"));
    }
}
