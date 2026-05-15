package com.gym.crm.application.service;

import com.gym.crm.application.entity.User;
import com.gym.crm.application.service.common.AuthenticationService;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.query.Query;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AuthenticationServiceTest {

    private final String USERNAME = "borys.burpee";
    private final String PASSWORD = "correct_password";

    @Mock
    private SessionFactory sessionFactory;

    @Mock
    private Session session;

    @Mock
    private Query<User> query;

    @Mock
    PasswordEncoder passwordEncoder;

    @InjectMocks
    private AuthenticationService authService;

    @Test
    @DisplayName("Should authenticate successfully when credentials are valid")
    void authenticate_success() {
        String rawPasswordInput = "rawPassword123";
        String encodedPasswordInDb = "hashedPassword789";

        User user = User.builder()
                .username(USERNAME)
                .password(encodedPasswordInDb)
                .build();

        when(sessionFactory.getCurrentSession()).thenReturn(session);
        when(session.createQuery(anyString(), eq(User.class))).thenReturn(query);
        when(query.setParameter("username", USERNAME)).thenReturn(query);
        when(query.uniqueResultOptional()).thenReturn(Optional.of(user));
        when(passwordEncoder.matches(rawPasswordInput, encodedPasswordInDb)).thenReturn(true);

        assertDoesNotThrow(() -> authService.authenticate(USERNAME, rawPasswordInput));

        verify(session).createQuery("FROM User WHERE username = :username", User.class);
        verify(query).setParameter("username", USERNAME);
        verify(passwordEncoder).matches(rawPasswordInput, encodedPasswordInDb);
    }

    @Test
    @DisplayName("Should throw RuntimeException when user is not found in database")
    void authenticate_userNotFound_throwsException() {
        when(sessionFactory.getCurrentSession()).thenReturn(session);
        when(session.createQuery(anyString(), eq(User.class))).thenReturn(query);
        when(query.setParameter("username", USERNAME)).thenReturn(query);
        when(query.uniqueResultOptional()).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class, () -> authService.authenticate(USERNAME, PASSWORD));

        assertEquals("User not found", exception.getMessage());
    }

    @Test
    @DisplayName("Should throw RuntimeException when password does not match")
    void authenticate_invalidPassword_throwsException() {
        String rawPasswordInput = "wrong_password";
        String encodedPasswordInDb = "hashed_password_in_db";

        User user = User.builder()
                .username(USERNAME)
                .password(encodedPasswordInDb)
                .build();

        when(sessionFactory.getCurrentSession()).thenReturn(session);
        when(session.createQuery(anyString(), eq(User.class))).thenReturn(query);
        when(query.setParameter("username", USERNAME)).thenReturn(query);
        when(query.uniqueResultOptional()).thenReturn(Optional.of(user));

        when(passwordEncoder.matches(rawPasswordInput, encodedPasswordInDb)).thenReturn(false);

        RuntimeException exception = assertThrows(RuntimeException.class, () -> authService.authenticate(USERNAME, "wrong_password"));

        assertEquals("Invalid username or password", exception.getMessage());
    }
}