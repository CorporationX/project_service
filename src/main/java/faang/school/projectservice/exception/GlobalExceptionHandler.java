package faang.school.projectservice.exception;

import com.amazonaws.services.kms.model.AlreadyExistsException;
import com.amazonaws.services.kms.model.NotFoundException;
import jakarta.persistence.EntityNotFoundException;
import jakarta.servlet.ServletException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import software.amazon.ion.NullValueException;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(NullValueException.class)
    @ResponseStatus(value = HttpStatus.BAD_REQUEST)
    public ErrorResponse handleNullValueException(NullValueException e) {
        log.error("Data validation exception occurred", e);
        return new ErrorResponse("Data validation exception occurred", e.getMessage());
    }

    @ExceptionHandler(NotFoundException.class)
    @ResponseStatus(value = HttpStatus.NOT_FOUND)
    public ErrorResponse handleNotFoundException(NotFoundException e) {
        log.error("Not found exception occurred", e);
        return new ErrorResponse("Not found exception occurred", e.getErrorMessage());
    }

    @ExceptionHandler(IllegalArgumentException.class)
    @ResponseStatus(value = HttpStatus.BAD_REQUEST)
    public ErrorResponse handleIllegalArgumentException(IllegalArgumentException e) {
        log.error("Illegal argument exception occurred", e);
        return new ErrorResponse("Illegal argument exception occurred", e.getMessage());
    }

    @ExceptionHandler(AlreadyExistsException.class)
    @ResponseStatus(value = HttpStatus.CONFLICT)
    public ErrorResponse handleAlreadyExistsException(AlreadyExistsException e) {
        log.error("Already exist exception occurred", e);
        return new ErrorResponse("Already exist exception occurred", e.getErrorMessage());
    }

    @ExceptionHandler(IllegalStateException.class)
    @ResponseStatus(value = HttpStatus.CONFLICT)
    public ErrorResponse handleAlreadyExistsException(IllegalStateException e) {
        log.error("Illegal state exception occurred", e);
        return new ErrorResponse("Illegal state exception occurred", e.getMessage());
    }

    @ExceptionHandler(EntityNotFoundException.class)
    @ResponseStatus(value = HttpStatus.NOT_FOUND)
    public ErrorResponse handleAlreadyExistsException(EntityNotFoundException e) {
        log.error("Entity not found exception occurred", e);
        return new ErrorResponse("Entity not found exception occurred", e.getMessage());
    }

    @ExceptionHandler(ServletException.class)
    @ResponseStatus(value = HttpStatus.NOT_FOUND)
    public ErrorResponse handleServletException(ServletException e) {
        log.error("Servlet exception occurred", e);
        return new ErrorResponse("Servlet exception occurred", e.getMessage());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Object> handleValidationExceptions(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach((error) -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });
        log.error("Validation exceptions occurred", ex);
        return ResponseEntity.badRequest().body(errors);
    }

    @ExceptionHandler(Exception.class)
    @ResponseStatus(value = HttpStatus.INTERNAL_SERVER_ERROR)
    public ErrorResponse handleException(Exception e) {
        log.error("Servlet exception occurred: ", e);
        return new ErrorResponse("Servlet exception occurred", e.getMessage());
    }

    @ExceptionHandler(StorageLimitException.class)
    @ResponseStatus(value = HttpStatus.INSUFFICIENT_STORAGE)
    public ErrorResponse handleStorageLimitExceededException(StorageLimitException e) {
        log.error("Storage limit exception occurred", e);
        return new ErrorResponse("Storage limit exception occurred", e.getMessage());
    }
}
