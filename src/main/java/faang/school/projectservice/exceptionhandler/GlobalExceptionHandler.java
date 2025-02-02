package faang.school.projectservice.exceptionhandler;

import faang.school.projectservice.exception.DataAlreadyExistException;
import faang.school.projectservice.exception.DataNotFoundException;
import faang.school.projectservice.exception.DataValidateException;
import faang.school.projectservice.exception.GoogleCalendarException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.HashMap;
import java.util.Map;

@ControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, String>> handleValidationExceptions(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getFieldErrors().forEach(error -> {
            errors.put(error.getField(), error.getDefaultMessage());
        });
        System.err.println("Validation error: " + errors);
        return ResponseEntity.badRequest().body(errors);
    }

    @ExceptionHandler(DataValidateException.class)
    public ResponseEntity<ErrorResponse> handlerDataValidateException(DataValidateException e) {
        log.error(e.getMessage());
        ErrorResponse response = new ErrorResponse(e.getMessage());
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(DataAlreadyExistException.class)
    public ResponseEntity<ErrorResponse> handlerDataAlreadyExistException(DataAlreadyExistException e) {
        log.error(e.getMessage());
        ErrorResponse response = new ErrorResponse(e.getMessage());
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(DataNotFoundException.class)
    public ResponseEntity<ErrorResponse> handlerDataNotFoundException(DataNotFoundException e) {
        log.error(e.getMessage());
        ErrorResponse response = new ErrorResponse(e.getMessage());
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(GoogleCalendarException.class)
    public ResponseEntity<ErrorResponse> handlerGoogleCalendarException(GoogleCalendarException e) {
        log.error(e.getMessage());
        ErrorResponse response = new ErrorResponse(e.getMessage());
        return new ResponseEntity<>(response, HttpStatus.SERVICE_UNAVAILABLE);
    }
}
