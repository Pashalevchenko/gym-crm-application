package com.gym.crm.application.service.impl;

import com.gym.crm.application.entity.User;
import com.gym.crm.application.exeption.AuthenticationException;
import com.gym.crm.application.service.AuthenticationService;
import lombok.AllArgsConstructor;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class AuthenticationServiceImpl implements AuthenticationService {
    private final SessionFactory sessionFactory;
    private final PasswordEncoder passwordEncoder;

    public void authenticate(String username, String password) {
        Session session = sessionFactory.getCurrentSession();

        User user = session.createQuery("FROM User WHERE username = :username", User.class)
                .setParameter("username", username)
                .uniqueResultOptional()
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new AuthenticationException("Invalid password");
        }
    }
}
