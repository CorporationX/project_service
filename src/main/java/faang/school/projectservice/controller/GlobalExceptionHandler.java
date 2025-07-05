package faang.school.projectservice.controller;

import faang.school.projectservice.exception.TaskValidationException;
import faang.school.projectservice.exception.TaskEntityNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler(TaskValidationException.class)
    public ResponseEntity<String> handleTaskValidationException(TaskValidationException ex) {
        log.error("Task validation error: {}", ex.getMessage());
        return new ResponseEntity<>(ex.getMessage(), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(TaskEntityNotFoundException.class)
    public ResponseEntity<String> handleEntityNotFoundException(TaskEntityNotFoundException ex) {
        log.warn("Entity not found: {}", ex.getMessage());
        return new ResponseEntity<>(ex.getMessage(), HttpStatus.NOT_FOUND);
    }
}