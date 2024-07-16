package uz.audio_book.backend.exceptions;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler(Exception.class)
    public HttpEntity<?> handleException(Exception e) {
        log.error(e.getMessage());
        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ExceptionResponse(
                        HttpStatus.INTERNAL_SERVER_ERROR,
                        "Something wrong happened in the server",
                        LocalDateTime.now()));
    }

    @ExceptionHandler(NotFoundException.class)
    public HttpEntity<?> handleNotFoundException(NotFoundException e) {
        log.error(e.getMessage());
        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(new ExceptionResponse(
                        HttpStatus.NOT_FOUND,
                        e.getMessage(),
                        LocalDateTime.now()
                ));
    }

    @ExceptionHandler(BadRequestException.class)
    public HttpEntity<?> handleAlreadyExistsException(BadRequestException e) {
        log.warn(e.getMessage());
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(new ExceptionResponse(
                        HttpStatus.BAD_REQUEST,
                        e.getMessage(),
                        LocalDateTime.now()
                ));
    }

    @ExceptionHandler(UserNotFoundException.class)
    public HttpEntity<?> handleUserNotFoundException(UserNotFoundException e) {
        log.warn(e.getMessage());
        return ResponseEntity
                .status(HttpStatus.UNAUTHORIZED)
                .body(new ExceptionResponse(
                        HttpStatus.UNAUTHORIZED,
                        e.getMessage(),
                        LocalDateTime.now()
                ));
    }

    @ExceptionHandler(HeaderException.class)
    public HttpEntity<?> handleHeaderException(HeaderException e) {
        log.error(e.getMessage());
        return ResponseEntity
                .status(HttpStatus.UNAUTHORIZED)
                .body(new ExceptionResponse(
                        HttpStatus.UNAUTHORIZED,
                        e.getMessage(),
                        LocalDateTime.now()
                ));
    }

    @ExceptionHandler(ContentNotFound.class)
    public HttpEntity<?> handleContentNOtFoundException(ContentNotFound e) {
        log.warn(e.getMessage());
        return ResponseEntity
                .status(HttpStatus.NO_CONTENT)
                .body(null);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<?> handleMethodArgumentNotValidException(MethodArgumentNotValidException e) {
        Map<String, Object> errors = new HashMap<>();

        e.getBindingResult().getAllErrors().forEach(error -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });

        log.error(e.getMessage(), e);
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(new ExceptionResponse(
                        HttpStatus.BAD_REQUEST,
                        "Check data's format",
                        LocalDateTime.now(),
                        errors));
    }

}
