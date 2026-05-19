package com.gym.crm.application.exception;

import com.gym.crm.application.openapi.ErrorResponse;
import jakarta.persistence.PersistenceException;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.HibernateException;
import org.springframework.dao.DataAccessException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.HandlerMethodValidationException;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleMethodArgumentNotValid(MethodArgumentNotValidException exception) {
        String details = exception.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(error -> error.getField() + " " + error.getDefaultMessage())
                .collect(Collectors.joining(", "));

        return buildResponse(ApiErrorCode.VALIDATION_ERROR, ApiErrorCode.VALIDATION_ERROR.getMessage() + ": " + details);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ErrorResponse> handleConstraintViolation(ConstraintViolationException exception) {
        String details = exception.getConstraintViolations()
                .stream()
                .map(violation -> violation.getPropertyPath() + " " + violation.getMessage())
                .collect(Collectors.joining(", "));

        return buildResponse(ApiErrorCode.VALIDATION_ERROR, ApiErrorCode.VALIDATION_ERROR.getMessage() + ": " + details);
    }

    @ExceptionHandler(HandlerMethodValidationException.class)
    public ResponseEntity<ErrorResponse> handleHandlerMethodValidation(HandlerMethodValidationException exception) {
        return buildResponse(ApiErrorCode.VALIDATION_ERROR, ApiErrorCode.VALIDATION_ERROR.getMessage() + ": invalid request parameters");
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorResponse> handleIllegalArgument(IllegalArgumentException exception) {
        return buildResponse(ApiErrorCode.VALIDATION_ERROR, ApiErrorCode.VALIDATION_ERROR.getMessage() + ": " + exception.getMessage());
    }

    @ExceptionHandler(NoSuchElementException.class)
    public ResponseEntity<ErrorResponse> handleNotFound(NoSuchElementException exception) {
        return buildResponse(ApiErrorCode.NOT_FOUND_ERROR, ApiErrorCode.NOT_FOUND_ERROR.getMessage() + ": " + exception.getMessage());
    }

    @ExceptionHandler(SecurityException.class)
    public ResponseEntity<ErrorResponse> handleAuthorization(SecurityException exception) {
        return buildResponse(ApiErrorCode.AUTHORIZATION_ERROR, ApiErrorCode.AUTHORIZATION_ERROR.getMessage());
    }

    @ExceptionHandler({
            DataAccessException.class,
            HibernateException.class,
            PersistenceException.class
    })
    public ResponseEntity<ErrorResponse> handleDatabaseException(Exception exception) {
        log.error("Database error occurred", exception);

        return buildResponse(ApiErrorCode.DATABASE_ERROR, ApiErrorCode.DATABASE_ERROR.getMessage());
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGenericException(Exception exception) {
        log.error("Unexpected application error occurred", exception);

        return buildResponse(ApiErrorCode.SERVICE_ERROR, ApiErrorCode.SERVICE_ERROR.getMessage());
    }

    @ExceptionHandler(AuthenticationFailedException.class)
    public ResponseEntity<ErrorResponse> handleAuthentication(AuthenticationFailedException exception) {
        return buildResponse(ApiErrorCode.AUTHENTICATION_ERROR, ApiErrorCode.AUTHENTICATION_ERROR.getMessage());
    }

    private ResponseEntity<ErrorResponse> buildResponse(ApiErrorCode errorCode, String message) {
        ErrorResponse response = new ErrorResponse()
                .errorCode(errorCode.getCode())
                .errorMessage(message);

        return ResponseEntity
                .status(errorCode.getStatus())
                .body(response);
    }
}