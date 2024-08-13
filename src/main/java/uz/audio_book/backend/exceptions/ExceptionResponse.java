package uz.audio_book.backend.exceptions;


import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.springframework.http.HttpStatus;

import java.time.LocalDateTime;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
public record ExceptionResponse(HttpStatus status, @JsonProperty("error_code") Integer errorCode, String message, String timestamp) {

    public ExceptionResponse(HttpStatus status, String message, LocalDateTime timestamp) {
        this(status, status.value(), message, timestamp.toString());
    }
}
