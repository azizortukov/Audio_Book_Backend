package uz.audio_book.backend.exceptions;


import com.fasterxml.jackson.annotation.JsonInclude;
import org.springframework.http.HttpStatus;

import java.time.LocalDateTime;
import java.util.Map;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
public record ExceptionResponse(HttpStatus status, String message, LocalDateTime timestamp, Map<String, Object> errors) {

    public ExceptionResponse(HttpStatus status,  String message, LocalDateTime timestamp) {
        this(status, message, timestamp, Map.of());
    }
}
