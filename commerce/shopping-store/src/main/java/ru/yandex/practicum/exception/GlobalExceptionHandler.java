package ru.yandex.practicum.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import ru.yandex.practicum.contains.Message;
import ru.yandex.practicum.dto.ProductNotFoundException;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<ProductNotFoundException> handleNotFoundException(NotFoundException ex) {
        return ResponseEntity.ok(ProductNotFoundException.builder()
                .cause(ex.getCause())
                .stackTrace(ex.getStackTrace())
                .httpStatus(HttpStatus.NOT_FOUND)
                .userMessage(Message.EXCEPTION_NOT_FOUND)
                .message(ex.getMessage())
                .suppressed(ex.getSuppressed())
                .localizedMessage(ex.getLocalizedMessage())
                .build());
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ProductNotFoundException> handleIllegalArgumentException(IllegalArgumentException ex) {
        return ResponseEntity.ok(ProductNotFoundException.builder()
                .cause(ex.getCause())
                .stackTrace(ex.getStackTrace())
                .httpStatus(HttpStatus.UNPROCESSABLE_ENTITY)
                .userMessage(Message.EXCEPTION_WRONG_ARGUMENT)
                .message(ex.getMessage())
                .suppressed(ex.getSuppressed())
                .localizedMessage(ex.getLocalizedMessage())
                .build());
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ProductNotFoundException> handleException(Exception ex) {
        return ResponseEntity.ok(ProductNotFoundException.builder()
                .cause(ex.getCause())
                .stackTrace(ex.getStackTrace())
                .httpStatus(HttpStatus.INTERNAL_SERVER_ERROR)
                .userMessage(Message.EXCEPTION_INNER_ERROR_SERVER)
                .message(ex.getMessage())
                .suppressed(ex.getSuppressed())
                .localizedMessage(ex.getLocalizedMessage())
                .build());
    }
}