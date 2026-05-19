package com.gym.crm.application.exception;

import com.gym.crm.application.openapi.ErrorResponse;
import jakarta.persistence.PersistenceException;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Path;
import org.hibernate.HibernateException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Set;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

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

    @Test
    @DisplayName("Should handle method argument not valid exception as validation error")
    void handleMethodArgumentNotValid_shouldReturnValidationError() {
        MethodArgumentNotValidException exception = mock(MethodArgumentNotValidException.class);
        BindingResult bindingResult = mock(BindingResult.class);
        FieldError firstNameError = new FieldError("traineeCreateRequest", "firstName", "must not be empty");
        FieldError lastNameError = new FieldError("traineeCreateRequest", "lastName", "must not be empty");

        when(exception.getBindingResult()).thenReturn(bindingResult);
        when(bindingResult.getFieldErrors()).thenReturn(List.of(firstNameError, lastNameError));

        ResponseEntity<ErrorResponse> response = handler.handleMethodArgumentNotValid(exception);

        assertEquals(400, response.getStatusCode().value());
        assertNotNull(response.getBody());
        assertEquals(2760, response.getBody().getErrorCode());
        assertEquals("Validation error: firstName must not be empty, lastName must not be empty", response.getBody().getErrorMessage());
    }

    @Test
    @DisplayName("Should handle constraint violation exception as validation error")
    void handleConstraintViolation_shouldReturnValidationError() {
        ConstraintViolationException exception = mock(ConstraintViolationException.class);

        ConstraintViolation<?> usernameViolation = mock(ConstraintViolation.class);
        Path usernamePath = mock(Path.class);
        ConstraintViolation<?> passwordViolation = mock(ConstraintViolation.class);
        Path passwordPath = mock(Path.class);

        when(usernameViolation.getPropertyPath()).thenReturn(usernamePath);
        when(usernamePath.toString()).thenReturn("username");
        when(usernameViolation.getMessage()).thenReturn("must not be blank");
        when(passwordViolation.getPropertyPath()).thenReturn(passwordPath);
        when(passwordPath.toString()).thenReturn("password");
        when(passwordViolation.getMessage()).thenReturn("must not be blank");
        when(exception.getConstraintViolations()).thenReturn(Set.of(usernameViolation, passwordViolation));

        ResponseEntity<ErrorResponse> response = handler.handleConstraintViolation(exception);

        assertEquals(400, response.getStatusCode().value());
        assertNotNull(response.getBody());
        assertEquals(2760, response.getBody().getErrorCode());
        assertThat(response.getBody().getErrorMessage())
                .startsWith("Validation error:")
                .contains("username must not be blank")
                .contains("password must not be blank");
    }

}