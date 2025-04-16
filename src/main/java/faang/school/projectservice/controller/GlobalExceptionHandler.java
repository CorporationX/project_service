package faang.school.projectservice.controller;

import faang.school.projectservice.excepion.DataValidationException;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {
    private static final String HANDLE_FORM = "Обработано исключение {}: {}";

    @ExceptionHandler(DataValidationException.class)
    public ResponseEntity<String> handleDataValidationException(DataValidationException ex) {
        log.error(HANDLE_FORM, "валидации данных", ex.getMessage(), ex);
        return badRequest(ex);
    }

    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<String> handleEntityNotFoundException(EntityNotFoundException ex) {
        log.error(HANDLE_FORM, "отсутствия сущности", ex.getMessage(), ex);
        return notFound(ex);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<String> handleConstraintViolationException(ConstraintViolationException ex) {
        log.error(HANDLE_FORM, "нарушения ограничений", ex.getMessage(), ex);
        return badRequest(ex);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Object> handleMethodArgumentNotValidException(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach((error) -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });
        log.error(HANDLE_FORM, "валидации аргументов метода", ex.getMessage(), ex);
        return ResponseEntity.badRequest().body(errors);
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<String> handleMethodArgumentTypeMismatchException(MethodArgumentTypeMismatchException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body("Invalid parameter: " + ex.getName() + " must be a number");
    }

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<String> handleRuntimeException(RuntimeException ex) {
        log.error(HANDLE_FORM, "в ходе работы программы", ex.getMessage(), ex);
        return internalServerError(ex);
    }

    private ResponseEntity<String> internalServerError(Exception ex) {
        return getResponse(ex, 500);
    }

    private ResponseEntity<String> badRequest(Exception ex) {
        return getResponse(ex, 400);
    }

    private ResponseEntity<String> notFound(Exception ex) {
        return getResponse(ex, 404);
    }

    private ResponseEntity<String> getResponse(Exception ex, int code) {
        return ResponseEntity
                .status(HttpStatus.valueOf(code))
                .body(ex.getMessage());
    }
}
