package faang.school.projectservice.exceptionhandler;

import faang.school.projectservice.exception.BadRequestException;
import faang.school.projectservice.exception.DataAlreadyExistException;
import faang.school.projectservice.exception.DataNotFoundException;
import faang.school.projectservice.exception.DataValidateException;
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
        Map<String, String> errorAttributes = new HashMap<>();
        ex.getBindingResult().getFieldErrors().forEach(error -> {
            errorAttributes.put(error.getField(), error.getDefaultMessage());
        });
        System.err.println("Validation error: " + errorAttributes);
        return ResponseEntity.badRequest().body(errorAttributes);
    }

    @ExceptionHandler(DataValidateException.class)
    public ResponseEntity<ErrorResponse> handleDataValidateException(DataValidateException e) {
        log.error(e.getMessage());
        ErrorResponse response = new ErrorResponse(e.getMessage());
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(DataAlreadyExistException.class)
    public ResponseEntity<ErrorResponse> handleDataAlreadyExistException(DataAlreadyExistException e) {
        log.error(e.getMessage());
        ErrorResponse response = new ErrorResponse(e.getMessage());
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(DataNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleDataNotFoundException(DataNotFoundException e) {
        log.error(e.getMessage());
        ErrorResponse response = new ErrorResponse(e.getMessage());
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(BadRequestException.class)
    public ResponseEntity<ErrorResponse> handleBadRequestException(BadRequestException e) {
        log.error(e.getMessage());
        ErrorResponse response = new ErrorResponse(e.getMessage());
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }
}
