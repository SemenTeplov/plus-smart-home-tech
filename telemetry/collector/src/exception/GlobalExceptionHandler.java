package exception;

import constants.Exceptions;

import dto.ErrorEvent;

import jakarta.validation.ValidationException;

import org.apache.kafka.common.errors.SerializationException;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;

@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(SerializationException.class)
    public ResponseEntity<ErrorEvent> handleSerializationException(SerializationException e) {
        ErrorEvent event = new ErrorEvent(
                Exceptions.EXCEPTION_SERIALIZATION,
                e.getMessage(),
                HttpStatus.CONFLICT,
                LocalDateTime.now());

        return ResponseEntity.status(HttpStatus.CONFLICT).body(event);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorEvent> handleMethodArgumentNotValidException(MethodArgumentNotValidException  e) {
        ErrorEvent event = new ErrorEvent(
                Exceptions.EXCEPTION_ARGUMENT_NOT_VALIDATION,
                e.getMessage(),
                HttpStatus.BAD_REQUEST,
                LocalDateTime.now());

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(event);
    }

    @ExceptionHandler(ValidationException.class)
    public ResponseEntity<ErrorEvent> handleValidationException(ValidationException e) {
        ErrorEvent event = new ErrorEvent(
                Exceptions.EXCEPTION_NOT_VALIDATION,
                e.getMessage(),
                HttpStatus.BAD_REQUEST,
                LocalDateTime.now());

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(event);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorEvent> handleException(Exception e) {
        ErrorEvent event = new ErrorEvent(
                Exceptions.EXCEPTION,
                e.getMessage(),
                HttpStatus.INTERNAL_SERVER_ERROR,
                LocalDateTime.now());

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(event);
    }
}
