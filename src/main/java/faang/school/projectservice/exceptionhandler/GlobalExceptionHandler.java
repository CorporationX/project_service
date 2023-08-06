package faang.school.projectservice.exceptionhandler;

import faang.school.projectservice.exception.DataAlreadyExistingException;
import faang.school.projectservice.exception.DataNotFoundException;
import faang.school.projectservice.exception.DataValidationException;
import faang.school.projectservice.exception.PrivateAccessException;
import jakarta.persistence.EntityExistsException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {
    @ExceptionHandler(DataAlreadyExistingException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<String> handlerDataAlreadyExistingException(DataAlreadyExistingException e) {
        log.error(e.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
    }

    @ExceptionHandler(DataNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ResponseEntity<String> handlerDataNotFoundException(DataNotFoundException e) {
        log.error(e.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
    }

    @ExceptionHandler(DataValidationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<String> handlerDataValidationException(DataValidationException e) {
        log.error(e.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
    }

    @ExceptionHandler(PrivateAccessException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ResponseEntity<String> handlerPrivateAccessException(PrivateAccessException e) {
        log.error(e.getMessage());
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<Map<String, String>> handlerMethodArgumentNotValidException(MethodArgumentNotValidException e) {
        Map<String, String> errors = new HashMap<>();
        e.getBindingResult().getAllErrors()
                .forEach(error -> {
                    String pathName = ((FieldError) error).getField();
                    String message = error.getDefaultMessage();
                    errors.put(pathName, message);
                });
        log.error(e.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errors);
    }

    @ExceptionHandler(RuntimeException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ResponseEntity<String> handlerRuntimeException(RuntimeException e) {
        log.error(e.getMessage());
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
    }

    @ExceptionHandler(EntityExistsException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ResponseEntity<String> handlerEntityNotFoundException(EntityExistsException e) {
        log.error(e.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
    }
}