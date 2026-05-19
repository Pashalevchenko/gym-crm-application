package com.gym.crm.application.exception;

import com.gym.crm.application.openapi.ErrorResponse;
import jakarta.persistence.PersistenceException;
import org.hibernate.HibernateException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.ResponseEntity;
import java.util.NoSuchElementException;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class GlobalExceptionHandlerTest {

    private final GlobalExceptionHandler handler = new GlobalExceptionHandler();

    @Test
    @DisplayName("Should handle illegal argument as validation error")
    void handleIllegalArgument_shouldReturnValidationError() {
        IllegalArgumentException exception = new IllegalArgumentException("username must not be blank");

        ResponseEntity<ErrorResponse> response = handler.handleIllegalArgument(exception);

        assertEquals(400, response.getStatusCode().value());
        assertNotNull(response.getBody());
        assertEquals(2760, response.getBody().getErrorCode());
        assertEquals("Validation error: username must not be blank", response.getBody().getErrorMessage());
    }

    @Test
    @DisplayName("Should handle not found exception")
    void handleNotFound_shouldReturnNotFoundError() {
        NoSuchElementException exception = new NoSuchElementException("Trainee with username test.user not found");

        ResponseEntity<ErrorResponse> response = handler.handleNotFound(exception);

        assertEquals(404, response.getStatusCode().value());
        assertNotNull(response.getBody());
        assertEquals(2835, response.getBody().getErrorCode());
        assertEquals("Requested data was not found: Trainee with username test.user not found", response.getBody().getErrorMessage());
    }

    @Test
    @DisplayName("Should handle security exception as authorization error")
    void handleAuthorization_shouldReturnAuthorizationError() {
        SecurityException exception = new SecurityException("User is not authorized");

        ResponseEntity<ErrorResponse> response = handler.handleAuthorization(exception);
        
        assertEquals(401, response.getStatusCode().value());
        assertNotNull(response.getBody());
        assertEquals(2806, response.getBody().getErrorCode());
        assertEquals("User is not authorized for request operation", response.getBody().getErrorMessage());
    }

    @Test
    @DisplayName("Should handle authentication failed exception")
    void handleAuthentication_shouldReturnAuthenticationError() {
        AuthenticationFailedException exception = new AuthenticationFailedException("Invalid username or password");

        ResponseEntity<ErrorResponse> response = handler.handleAuthentication(exception);

        assertEquals(401, response.getStatusCode().value());
        assertNotNull(response.getBody());
        assertEquals(2805, response.getBody().getErrorCode());
        assertEquals("Authentication fails", response.getBody().getErrorMessage());
    }

    @Test
    @DisplayName("Should handle Hibernate exception as database error")
    void handleDatabaseException_whenHibernateException_shouldReturnDatabaseError() {
        HibernateException exception = new HibernateException("Database failed");

        ResponseEntity<ErrorResponse> response = handler.handleDatabaseException(exception);

        assertEquals(500, response.getStatusCode().value());
        assertNotNull(response.getBody());
        assertEquals(3358, response.getBody().getErrorCode());
        assertEquals("Unexpected database access failure", response.getBody().getErrorMessage());
    }

    @Test
    @DisplayName("Should handle persistence exception as database error")
    void handleDatabaseException_whenPersistenceException_shouldReturnDatabaseError() {
        PersistenceException exception = new PersistenceException("Persistence failed");

        ResponseEntity<ErrorResponse> response = handler.handleDatabaseException(exception);

        assertEquals(500, response.getStatusCode().value());
        assertNotNull(response.getBody());
        assertEquals(3358, response.getBody().getErrorCode());
        assertEquals("Unexpected database access failure", response.getBody().getErrorMessage());
    }

    @Test
    @DisplayName("Should handle generic exception as service error")
    void handleGenericException_shouldReturnServiceError() {
        Exception exception = new RuntimeException("Unexpected error");

        ResponseEntity<ErrorResponse> response = handler.handleGenericException(exception);

        assertEquals(500, response.getStatusCode().value());
        assertNotNull(response.getBody());
        assertEquals(3200, response.getBody().getErrorCode());
        assertEquals("Internal processing error", response.getBody().getErrorMessage());
    }
}