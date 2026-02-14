package app.dto;

import org.springframework.http.HttpStatus;

import java.time.LocalDateTime;

public record ErrorEvent(
        String message,
        String reason,
        HttpStatus status,
        LocalDateTime createAt
) {

}
