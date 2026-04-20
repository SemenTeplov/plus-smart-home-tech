package ru.yandex.practicum.dto;

import lombok.Builder;
import org.springframework.http.HttpStatus;

@Builder
public record ExceptionDto(
        Throwable cause,
        StackTraceElement[] stackTrace,
        HttpStatus httpStatus,
        String userMessage,
        String message,
        Throwable[] suppressed,
        String localizedMessage
) {

}
