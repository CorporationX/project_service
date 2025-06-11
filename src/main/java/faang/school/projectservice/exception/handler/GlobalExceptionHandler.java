package faang.school.projectservice.exception.handler;

import jakarta.servlet.ServletException;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.ObjectNotFoundException;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Object> handleValidationExceptions(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach((error) -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });
        log.error(ex.getMessage());
        return ResponseEntity.badRequest().body(errors);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Object> handleIllegalArgumentExceptions(IllegalArgumentException ex) {
        return badRequest(ex);
    }

    @ExceptionHandler(IOException.class)
    public ResponseEntity<Object> handleIOExceptions(IOException ex) {
        return badRequest(ex);
    }

    @ExceptionHandler(ServletException.class)
    public ResponseEntity<Object> handleServletExceptions(ServletException ex) {
        return internalServerError(ex);
    }

    @ExceptionHandler(NullPointerException.class)
    public ResponseEntity<Object> handleNullPointerExceptions(NullPointerException ex) {
        return internalServerError(ex);
    }

    @ExceptionHandler
    public ResponseEntity<Object> handleObjectNotFoundExceptions(ObjectNotFoundException ex) {
        log.error(ex.getMessage());
        return ResponseEntity
                .notFound()
                .build();
    }


    private ResponseEntity<Object> internalServerError(Exception ex) {
        log.error(ex.getMessage());
        return ResponseEntity
                .internalServerError()
                .body(ex.getStackTrace());
    }

    private ResponseEntity<Object> badRequest(Exception ex) {
        log.error(ex.getMessage());
        return ResponseEntity
                .badRequest()
                .body(ex.getStackTrace());
    }
}