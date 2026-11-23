package com.github.maxbalan.smtp.dummy.server.controller;

import com.github.maxbalan.smtp.dummy.server.controller.email.EmailException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolationException;
import java.time.Clock;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.HandlerMethodValidationException;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.resource.NoResourceFoundException;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger LOGGER = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    private final Clock clock;

    public GlobalExceptionHandler(Clock clock) {
        this.clock = clock;
    }

    @ExceptionHandler(EmailException.class)
    ResponseEntity<ApiError> handleEmailException(
            EmailException exception, HttpServletRequest request) {
        return response(HttpStatus.BAD_REQUEST,
                        exception.code(),
                        exception.getMessage(),
                        request.getRequestURI());
    }

    @ExceptionHandler({
        ConstraintViolationException.class,
        HandlerMethodValidationException.class,
        MethodArgumentTypeMismatchException.class,
        MissingServletRequestParameterException.class
    })
    ResponseEntity<ApiError> handleInvalidRequest(Exception exception, HttpServletRequest request) {
        return response(HttpStatus.BAD_REQUEST,
                        "invalid_request",
                        exception.getMessage(),
                        request.getRequestURI());
    }

    @ExceptionHandler(NoResourceFoundException.class)
    ResponseEntity<ApiError> handleNotFound(
            NoResourceFoundException exception, HttpServletRequest request) {
        return response(HttpStatus.NOT_FOUND,
                        "not_found",
                        "The requested resource was not found",
                        request.getRequestURI());
    }

    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    ResponseEntity<ApiError> handleMethodNotAllowed(
            HttpRequestMethodNotSupportedException exception, HttpServletRequest request) {
        return response(HttpStatus.METHOD_NOT_ALLOWED,
                        "method_not_allowed",
                        exception.getMessage(),
                        request.getRequestURI());
    }

    @ExceptionHandler(Exception.class)
    ResponseEntity<ApiError> handleUnexpectedException(
            Exception exception, HttpServletRequest request) {
        LOGGER.error("Unexpected request failure [path={}]", request.getRequestURI(), exception);

        return response(HttpStatus.INTERNAL_SERVER_ERROR,
                        "internal_error",
                        "Unexpected server error",
                        request.getRequestURI());
    }

    private ResponseEntity<ApiError> response(HttpStatus status,
                                              String code,
                                              String detail,
                                              String instance) {
        var error = new ApiError("https://dummy-smtp-server.local/problems/" + code,
                                 status.getReasonPhrase(),
                                 status.value(),
                                 detail,
                                 instance,
                                 code,
                                 clock.instant());

        return ResponseEntity.status(status)
                             .contentType(MediaType.APPLICATION_PROBLEM_JSON)
                             .body(error);
    }
}
