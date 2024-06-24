package faang.school.projectservice.exception;

import faang.school.projectservice.exception.aws.s3.S3Exception;
import jakarta.validation.ConstraintViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(EntityNotFoundException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<String> handleEntityNotFoundException(EntityNotFoundException ex) {
        return ResponseEntity.badRequest().body(ex.getMessage());
    }
    
    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<String> handleRuntimeException(RuntimeException ex) {
        return ResponseEntity.internalServerError().body(ex.getMessage());
    }
    
    @ExceptionHandler(S3Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ResponseEntity handleS3Exceptions(RuntimeException ex) {
        return ResponseEntity.internalServerError().body(ex.getMessage());
    }
    
    
    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<String> handleRuntimeException(RuntimeException ex) {
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ResponseEntity handleS3Exceptions(RuntimeException ex) {
        return ResponseEntity.internalServerError().body(ex.getMessage());
    }
        
        @ExceptionHandler(FileException.class)
        @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
        public ResponseEntity handleFileExceptions(FileException ex) {
            return ResponseEntity.internalServerError().body(ex.getMessage());
        }
        
        @ExceptionHandler(MethodArgumentNotValidException.class)
        @ResponseStatus(HttpStatus.BAD_REQUEST)
        public ResponseEntity handleValidationExceptions(MethodArgumentNotValidException ex) {
            Map<String, String> errors = ex.getBindingResult()
                .getAllErrors()
                .stream()
                .collect(Collectors.toMap(error -> ((FieldError) error).getField(), error -> error.getDefaultMessage()));
            return ResponseEntity.badRequest().body(errors);
        }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<String> handleConstraintViolationException(ConstraintViolationException constraintViolationException) {
        return ResponseEntity.badRequest().body(constraintViolationException.getMessage());
    }
}
