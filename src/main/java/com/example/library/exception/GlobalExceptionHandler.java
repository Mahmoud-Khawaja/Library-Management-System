package com.example.library.exception;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.NoHandlerFoundException;

import java.time.LocalDateTime;
import java.util.List;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(NoHandlerFoundException.class)
    public ResponseEntity<ApiError> handleNoHandlerFound(NoHandlerFoundException ex, HttpServletRequest request) {
        return buildErrorResponse(HttpStatus.NOT_FOUND, "Route not found", request.getRequestURI(), null);
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ApiError> handleResourceNotFound(ResourceNotFoundException ex, HttpServletRequest request) {
        return buildErrorResponse(HttpStatus.NOT_FOUND, ex.getMessage(), request.getRequestURI(), null);
    }

    @ExceptionHandler(DuplicateResourceException.class)
    public ResponseEntity<ApiError> handleDuplicateResource(DuplicateResourceException ex, HttpServletRequest request) {
        return buildErrorResponse(HttpStatus.CONFLICT, ex.getMessage(), request.getRequestURI(), null);
    }

    @ExceptionHandler(BookAlreadyBorrowedException.class)
    public ResponseEntity<ApiError> handleBookAlreadyBorrowed(BookAlreadyBorrowedException ex, HttpServletRequest request) {
        return buildErrorResponse(HttpStatus.CONFLICT, ex.getMessage(), request.getRequestURI(), null);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiError> handleValidationErrors(MethodArgumentNotValidException ex, HttpServletRequest request) {
        List<ValidationError> validationErrors = ex.getBindingResult().getFieldErrors().stream()
                .map(fieldError -> new ValidationError(fieldError.getField(), fieldError.getDefaultMessage()))
                .toList();

        return buildErrorResponse(HttpStatus.BAD_REQUEST, "Validation failed", request.getRequestURI(), validationErrors);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ApiError> handleIllegalArgument(IllegalArgumentException ex, HttpServletRequest request) {
        return buildErrorResponse(HttpStatus.BAD_REQUEST, ex.getMessage(), request.getRequestURI(), null);
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ApiError> handleDataIntegrityViolation(DataIntegrityViolationException ex, HttpServletRequest request) {
        return buildErrorResponse(
                HttpStatus.CONFLICT,
                "A resource with the given data already exists (duplicate key constraint).",
                request.getRequestURI(),
                null
        );
    }

    @ExceptionHandler(NullPointerException.class)
    public ResponseEntity<ApiError> handleNullPointer(NullPointerException ex, HttpServletRequest request) {
        return buildErrorResponse(HttpStatus.BAD_REQUEST, "A required value was null: " + ex.getMessage(), request.getRequestURI(), null);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiError> handleGeneral(Exception ex, HttpServletRequest request) {
        return buildErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR, "Unexpected error", request.getRequestURI(), null);
    }

    private ResponseEntity<ApiError> buildErrorResponse(
            HttpStatus status,
            String message,
            String path,
            List<ValidationError> validationErrors
    ) {
        ApiError apiError = new ApiError(
                LocalDateTime.now(),
                status.value(),
                status.getReasonPhrase(),
                message,
                path,
                validationErrors
        );
        return ResponseEntity.status(status).body(apiError);
    }
}
