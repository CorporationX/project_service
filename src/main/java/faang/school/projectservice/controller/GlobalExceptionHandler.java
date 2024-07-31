package faang.school.projectservice.controller;

import faang.school.projectservice.exceptions.DataValidationException;
import faang.school.projectservice.exceptions.ErrorMessage;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(DataValidationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorMessage handleDataValidationException(DataValidationException e, WebRequest request) {
        return ErrorMessage.builder()
                .timestamp(LocalDateTime.now())
                .message(e.getMessage())
                .statusCode(HttpStatus.BAD_REQUEST.value())
                .description(request.getDescription(false)).build();
    }

    @ExceptionHandler(EntityNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorMessage handleEntityNotFoundException(EntityNotFoundException e, WebRequest request) {
        return ErrorMessage.builder()
                .timestamp(LocalDateTime.now())
                .message(e.getMessage())
                .statusCode(HttpStatus.BAD_REQUEST.value())
                .description(request.getDescription(false)).build();
    }

    @ExceptionHandler(RuntimeException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ErrorMessage handleRuntimeException(RuntimeException e, WebRequest request) {
        return ErrorMessage.builder()
                .timestamp(LocalDateTime.now())
                .message(e.getMessage())
                .statusCode(HttpStatus.INTERNAL_SERVER_ERROR.value())
                .description(request.getDescription(false)).build();
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<Object> handleMethodArgumentNotValidException(MethodArgumentNotValidException e) {
        Map<String, String> errors = new HashMap<>();
        e.getBindingResult().getAllErrors().forEach((error) -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });
        return ResponseEntity.badRequest().body(errors);
    }
}
