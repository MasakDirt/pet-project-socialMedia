package com.social.media.exception;

import com.social.media.model.dto.error.ErrorResponse;
import jakarta.persistence.EntityNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.exception.ConstraintViolationException;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.server.ResponseStatusException;

import jakarta.servlet.http.HttpServletRequest;

import java.time.LocalDateTime;
import java.util.stream.Collectors;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(ResponseStatusException.class)
    public ResponseEntity<ErrorResponse> handleResponseStatusException(HttpServletRequest request, ResponseStatusException ex) {
        return getErrorResponse(request, ex.getStatusCode(), ex.getReason());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleMethodArgumentNotValidException(HttpServletRequest request, MethodArgumentNotValidException ex) {
        String message = ex.getFieldErrors()
                .stream()
                .map(DefaultMessageSourceResolvable::getDefaultMessage)
                .collect(Collectors.joining(", "));

        return getErrorResponse(request, HttpStatus.BAD_REQUEST, message);
    }

    @ExceptionHandler({ConstraintViolationException.class, SameUsersException.class,
            InvalidTextException.class, LastPhotoException.class})
    public ResponseEntity<ErrorResponse> handleConstraintViolationException(HttpServletRequest request, Exception ex) {
        return getErrorResponse(request, HttpStatus.BAD_REQUEST, ex.getMessage());
    }

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<ErrorResponse> handleBadCredentialsException(HttpServletRequest request, BadCredentialsException ex) {
        return getErrorResponse(request, HttpStatus.UNAUTHORIZED, ex.getMessage());
    }

    @ExceptionHandler({AccessDeniedException.class, LikeAlreadyExistException.class})
    public ResponseEntity<ErrorResponse> handleAccessDeniedException(HttpServletRequest request, RuntimeException ex) {
        return getErrorResponse(request, HttpStatus.FORBIDDEN, ex.getMessage());
    }

    @ExceptionHandler({EntityNotFoundException.class, PhotoInBucketNotFound.class, PhotoDoesNotExist.class})
    public ResponseEntity<ErrorResponse> handleNotFoundExceptions(HttpServletRequest request, RuntimeException ex) {
        return getErrorResponse(request, HttpStatus.NOT_FOUND, ex.getMessage());
    }

    @ExceptionHandler({PostCreationException.class, BucketCreationException.class, PhotoGettingException.class})
    public ResponseEntity<ErrorResponse> handleCreationException(HttpServletRequest request, RuntimeException ex) {
        return getErrorResponse(request, HttpStatus.INTERNAL_SERVER_ERROR, ex.getMessage());
    }

    @ExceptionHandler(ConnectionToMinIOFailed.class)
    public ResponseEntity<ErrorResponse> handleConnectionToMinIOFailed(HttpServletRequest request, ConnectionToMinIOFailed ex) {
        return getErrorResponse(request, HttpStatus.INTERNAL_SERVER_ERROR, ex.getMessage());
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleException(HttpServletRequest request, Exception ex) {
        return getErrorResponse(request, HttpStatus.INTERNAL_SERVER_ERROR, ex.getMessage());
    }

    private ResponseEntity<ErrorResponse> getErrorResponse(HttpServletRequest request, HttpStatusCode httpStatus, String message) {
        log.error("Exception raised = {} :: URL = {}", message, request.getRequestURL());
        return ResponseEntity.status(httpStatus)
                .body(new ErrorResponse(
                                LocalDateTime.now(),
                                httpStatus,
                                message,
                                request.getRequestURL().toString()
                        )
                );
    }
}
