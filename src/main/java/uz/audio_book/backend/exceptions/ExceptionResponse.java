package uz.audio_book.backend.exceptions;


import com.fasterxml.jackson.annotation.JsonInclude;
import org.springframework.http.HttpStatus;

import java.util.Map;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
public record ExceptionResponse(HttpStatus status, Integer responseType, String message, String timestamp, Map<String, Object> errors) {

    public ExceptionResponse(HttpStatus status, String message, String timestamp) {
        this(status, status.value(), message, timestamp, Map.of());
    }

    public ExceptionResponse(HttpStatus status, String message, String timestamp, Map<String, Object> errors) {
        this(status, status.value(), message, timestamp, errors);
    }
}
