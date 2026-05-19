package com.gym.crm.application.service.common;

import com.gym.crm.application.entity.User;
import com.gym.crm.application.exception.AuthenticationFailedException;
import lombok.AllArgsConstructor;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import java.util.NoSuchElementException;

@Service
@AllArgsConstructor
public class AuthenticationService {
    private final SessionFactory sessionFactory;
    private final PasswordEncoder passwordEncoder;

    public void authenticate(String username, String password) {
        Session session = sessionFactory.getCurrentSession();

        User user = session.createQuery("FROM User WHERE username = :username", User.class)
                .setParameter("username", username)
                .uniqueResultOptional()
                .orElseThrow(() -> new NoSuchElementException("User not found"));

        verifyPassword(password, user.getPassword(), "Invalid username or password");
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
