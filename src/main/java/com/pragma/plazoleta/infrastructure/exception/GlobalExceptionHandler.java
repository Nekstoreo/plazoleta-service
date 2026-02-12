package com.pragma.plazoleta.infrastructure.exception;

import com.pragma.plazoleta.domain.exception.*;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingRequestHeaderException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.stream.Collectors;

@RestControllerAdvice
public class GlobalExceptionHandler {

        private static final String BAD_REQUEST = "Bad Request";
        private static final String VALIDATION_ERROR = "Validation Error";

        @ExceptionHandler({
                        InvalidRestaurantNameException.class,
                        InvalidNitException.class,
                        InvalidPhoneException.class,
                        InvalidPriceException.class,
                        InvalidActiveStatusException.class,
                        EmptyOrderException.class,
                        InvalidQuantityException.class,
                        DishNotFromRestaurantException.class,
                        DishNotActiveException.class,
                        InvalidOrderStatusException.class,
                        OrderNotInPreparationException.class,
                        InvalidSecurityPinException.class,
                        OrderNotCancellableException.class
        })
        public ResponseEntity<ErrorResponse> handleValidationExceptions(
                        RuntimeException ex, HttpServletRequest request) {
                ErrorResponse error = new ErrorResponse(
                                LocalDateTime.now(),
                                HttpStatus.BAD_REQUEST.value(),
                                VALIDATION_ERROR,
                                ex.getMessage(),
                                request.getRequestURI());
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
        }

        @ExceptionHandler({
                        OwnerNotFoundException.class,
                        RestaurantNotFoundException.class,
                        DishNotFoundException.class,
                        EmployeeNotAssociatedWithRestaurantException.class,
                        OrderNotFoundException.class,
                        ClientPhoneNotFoundException.class
        })
        public ResponseEntity<ErrorResponse> handleNotFoundException(
                        RuntimeException ex, HttpServletRequest request) {
                ErrorResponse error = new ErrorResponse(
                                LocalDateTime.now(),
                                HttpStatus.NOT_FOUND.value(),
                                "Not Found",
                                ex.getMessage(),
                                request.getRequestURI());
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
        }

        @ExceptionHandler({
                        UserNotOwnerException.class,
                        UserNotRestaurantOwnerException.class
        })
        public ResponseEntity<ErrorResponse> handleForbiddenException(
                        RuntimeException ex, HttpServletRequest request) {
                ErrorResponse error = new ErrorResponse(
                                LocalDateTime.now(),
                                HttpStatus.FORBIDDEN.value(),
                                "Forbidden",
                                ex.getMessage(),
                                request.getRequestURI());
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(error);
        }

        @ExceptionHandler({
                        RestaurantAlreadyExistsException.class,
                        ClientHasActiveOrderException.class
        })
        public ResponseEntity<ErrorResponse> handleConflictException(
                        RuntimeException ex, HttpServletRequest request) {
                ErrorResponse error = new ErrorResponse(
                                LocalDateTime.now(),
                                HttpStatus.CONFLICT.value(),
                                "Conflict",
                                ex.getMessage(),
                                request.getRequestURI());
                return ResponseEntity.status(HttpStatus.CONFLICT).body(error);
        }

        @ExceptionHandler(IllegalArgumentException.class)
        public ResponseEntity<ErrorResponse> handleIllegalArgumentException(
                        IllegalArgumentException ex, HttpServletRequest request) {
                ErrorResponse error = new ErrorResponse(
                                LocalDateTime.now(),
                                HttpStatus.BAD_REQUEST.value(),
                                BAD_REQUEST,
                                ex.getMessage(),
                                request.getRequestURI());
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
        }

        @ExceptionHandler(MethodArgumentNotValidException.class)
        public ResponseEntity<ErrorResponse> handleMethodArgumentNotValidException(
                        MethodArgumentNotValidException ex, HttpServletRequest request) {
                // Use first validation message as main message for consistency
                String message = ex.getBindingResult().getFieldErrors().stream()
                                .map(FieldError::getDefaultMessage)
                                .findFirst()
                                .orElse("Input data validation error");

                ErrorResponse error = new ErrorResponse(
                                LocalDateTime.now(),
                                HttpStatus.BAD_REQUEST.value(),
                                VALIDATION_ERROR,
                                message,
                                request.getRequestURI());
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
        }

        @ExceptionHandler(MissingRequestHeaderException.class)
        public ResponseEntity<ErrorResponse> handleMissingRequestHeaderException(
                        MissingRequestHeaderException ex, HttpServletRequest request) {
                ErrorResponse error = new ErrorResponse(
                                LocalDateTime.now(),
                                HttpStatus.BAD_REQUEST.value(),
                                BAD_REQUEST,
                                "Required header '" + ex.getHeaderName() + "' is missing",
                                request.getRequestURI());
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
        }

        @ExceptionHandler(Exception.class)
        public ResponseEntity<ErrorResponse> handleGenericException(
                        Exception ex, HttpServletRequest request) {
                ErrorResponse error = new ErrorResponse(
                                LocalDateTime.now(),
                                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                                "Internal Server Error",
                                "An unexpected error occurred",
                                request.getRequestURI());
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
}
