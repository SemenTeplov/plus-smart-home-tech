package ru.yandex.practicum.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import ru.yandex.practicum.constant.Message;
import ru.yandex.practicum.dto.ExceptionDto;

@RestControllerAdvice
public class GlobalException {

    @ExceptionHandler(NotEmptyNameException.class)
    public ResponseEntity<ExceptionDto> handleNotEmptyNameException(NotEmptyNameException ex) {
        return ResponseEntity.ok(ExceptionDto.builder()
                .cause(ex.getCause())
                .stackTrace(ex.getStackTrace())
                .httpStatus(HttpStatus.UNAUTHORIZED)
                .userMessage(Message.EXCEPTION_NAME_IS_NOT_EMPTY)
                .message(ex.getMessage())
                .suppressed(ex.getSuppressed())
                .localizedMessage(ex.getLocalizedMessage())
                .build());
    }

    @ExceptionHandler(NotEmptyProductException.class)
    public ResponseEntity<ExceptionDto> handleNotEmptyProductException(NotEmptyProductException ex) {
        return ResponseEntity.ok(ExceptionDto.builder()
                .cause(ex.getCause())
                .stackTrace(ex.getStackTrace())
                .httpStatus(HttpStatus.BAD_REQUEST)
                .userMessage(Message.EXCEPTION_PRODUCT_IS_NOT_EMPTY)
                .message(ex.getMessage())
                .suppressed(ex.getSuppressed())
                .localizedMessage(ex.getLocalizedMessage())
                .build());
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ExceptionDto> handleIllegalArgumentException(IllegalArgumentException ex) {
        return ResponseEntity.ok(ExceptionDto.builder()
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
    public ResponseEntity<ExceptionDto> handleException(Exception ex) {
        return ResponseEntity.ok(ExceptionDto.builder()
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