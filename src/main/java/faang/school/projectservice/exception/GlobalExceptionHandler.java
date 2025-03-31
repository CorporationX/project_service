package faang.school.projectservice.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Object> handleMethodArgumentNotValidException(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getFieldErrors().forEach(error -> {
            errors.put(error.getField(), error.getDefaultMessage());
        });
        log.error("Validation failed: {}", errors);
        return buildErrorResponseEntity(HttpStatus.BAD_REQUEST, "Validation failed", errors);
    }

    @ExceptionHandler(BadRequestException.class)
    public ResponseEntity<Object> handleBadRequestException(BadRequestException ex) {
        String message = ex.getMessage();
        log.error("Bad request: {}", message, ex);
        return buildErrorResponseEntity(HttpStatus.BAD_REQUEST, message, null);
    }

    @ExceptionHandler(DataAlreadyExistException.class)
    public ResponseEntity<Object> handleDataAlreadyExistException(DataAlreadyExistException ex) {
        String message = ex.getMessage();
        log.error("Data already exist: {}", message, ex);
        return buildErrorResponseEntity(HttpStatus.BAD_REQUEST, message, null);
    }

    @ExceptionHandler(DataNotFoundException.class)
    public ResponseEntity<Object> handleDataNotFoundException(DataNotFoundException ex) {
        String message = ex.getMessage();
        log.error("Data not found: {}", message, ex);
        return buildErrorResponseEntity(HttpStatus.BAD_REQUEST, message, null);
    }

    @ExceptionHandler(DataValidationException.class)
    public ResponseEntity<Object> handleDataValidationException(DataValidationException ex) {
        String message = ex.getMessage();
        log.error("Data validation failed: {}", message, ex);
        return buildErrorResponseEntity(HttpStatus.BAD_REQUEST, message, null);
    }

    @ExceptionHandler(MinioException.class)
    public ResponseEntity<Object> handleMinioException(MinioException ex) {
        String message = ex.getMessage();
        log.error("Minio exception: {}", message, ex);
        return buildErrorResponseEntity(HttpStatus.BAD_REQUEST, "Internal server error", null);
    }

    @ExceptionHandler(ProjectNotFoundException.class)
    public ResponseEntity<Object> handleProjectNotFoundException(ProjectNotFoundException ex) {
        String message = ex.getMessage();
        log.error("Project not found: {}", message, ex);
        return buildErrorResponseEntity(HttpStatus.BAD_REQUEST, message, null);
    }

    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<Object> handleUserNotFoundException(UserNotFoundException ex) {
        String message = ex.getMessage();
        log.error("User not found: {}", message, ex);
        return buildErrorResponseEntity(HttpStatus.BAD_REQUEST, message, null);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Object> handleException(Exception ex) {
        log.error("Internal server error: {}", ex.getMessage(), ex);
        return buildErrorResponseEntity(HttpStatus.INTERNAL_SERVER_ERROR, "Internal server error", null);
    }

    private ResponseEntity<Object> buildErrorResponseEntity(
            HttpStatus status, String message, Map<String, String> errors) {
        ErrorResponse apiError = new ErrorResponse(status.value(), message, errors);
        return ResponseEntity.status(status).body(apiError);
    }
}
