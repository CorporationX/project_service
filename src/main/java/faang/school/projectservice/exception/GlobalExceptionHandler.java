package faang.school.projectservice.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {
    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<String> handleEntityNotFoundException(EntityNotFoundException e) {
        log.warn("Entity not found error: {}", e.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
    }

    @ExceptionHandler(EntityIdMismatchException.class)
    public ResponseEntity<String> handleEntityIdMismatchException(EntityIdMismatchException e) {
        log.warn("Entity validation error: {}", e.getMessage());
        return ResponseEntity.badRequest().body(e.getMessage());
    }

    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<String> handleUserNotFoundException(UserNotFoundException e) {
        log.warn("Author not found error: {}", e.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
    }

    @ExceptionHandler(EntityAlreadyExistException.class)
    public ResponseEntity<String> handleEntityAlreadyExistException(EntityAlreadyExistException e) {
        log.warn("Entity conflict error: {}", e.getMessage());
        return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());
    }

    @ExceptionHandler(DtoValidationException.class)
    public ResponseEntity<String> handleDtoValidationException(DtoValidationException e) {
        log.warn("Dto validation error: {}", e.getMessage());
        return ResponseEntity.badRequest().body(e.getMessage());
    }

    @ExceptionHandler(ForbiddenException.class)
    public ResponseEntity<String> handleForbiddenException(ForbiddenException e) {
        log.warn("Forbidden error: {}", e.getMessage());
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(e.getMessage());
    }

    @ExceptionHandler(BadRequestArgumentException.class)
    public ResponseEntity<String> handleBadRequestArgumentException(BadRequestArgumentException e) {
        log.warn("Request argument error: {}", e.getMessage());
        return ResponseEntity.badRequest().body(e.getMessage());
    }
}
