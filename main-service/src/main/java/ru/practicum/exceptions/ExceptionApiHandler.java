package ru.practicum.exceptions;

import lombok.extern.slf4j.Slf4j;
import org.hibernate.exception.ConstraintViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

@RestControllerAdvice
@Slf4j
public class ExceptionApiHandler {
    @ExceptionHandler({EntityNotFound.class})
    public ResponseEntity<ApiError> notFound(RuntimeException e) {
        HttpStatus status = HttpStatus.NOT_FOUND;
        String reason = "The required object was not found.";
        String message = e.getMessage();
        ApiError apiError = new ApiError(status.value(), reason, message, getTimestamp());
        return new ResponseEntity<>(apiError, status);
    }

    @ExceptionHandler({ConstraintViolationException.class, Conflict.class})
    public ResponseEntity<ApiError> conflict(RuntimeException e) {
        HttpStatus status = HttpStatus.CONFLICT;
        String reason = "Integrity constraint has been violated.";
        String message = e.getMessage();
        ApiError apiError = new ApiError(status.value(), reason, message, getTimestamp());
        return new ResponseEntity<>(apiError, status);
    }

    @ExceptionHandler({Forbidden.class})
    public ResponseEntity<ApiError> forbidden(Forbidden e) {
        HttpStatus status = HttpStatus.FORBIDDEN;
        String reason = "For the requested operation the conditions are not met.";
        String message = e.getMessage();
        ApiError apiError = new ApiError(status.value(), reason, message, getTimestamp());
        return new ResponseEntity<>(apiError, status);
    }

    @ExceptionHandler({MethodArgumentNotValidException.class})
    public ResponseEntity<ApiError> fieldValidation(MethodArgumentNotValidException e) {
        HttpStatus status = HttpStatus.BAD_REQUEST;
        String reason = "Incorrectly made request.";
        String message = "Field: category. Error: must not be blank. Value: null";
        ApiError apiError = new ApiError(status.value(), reason, message, getTimestamp());
        return new ResponseEntity<>(apiError, status);
    }
    private String getTimestamp() {
        DateTimeFormatter formatter = DateTimeFormatter.ISO_OFFSET_DATE_TIME;
        return formatter.format(ZonedDateTime.now());
    }
}
