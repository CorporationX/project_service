package faang.school.projectservice.handler;

import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.ValidationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler
    public ResponseEntity<ResponseError> handleIllegalArgumentException(IllegalArgumentException ex) {
        HttpStatus httpStatus = HttpStatus.BAD_REQUEST;
        return ResponseEntity
                .status(httpStatus)
                .body(new ResponseError(httpStatus.value(), ex.getMessage()));
    }

    @ExceptionHandler
    public ResponseEntity<ResponseError> handleEntityNotFoundException(EntityNotFoundException ex) {
        HttpStatus httpStatus = HttpStatus.NOT_FOUND;
        return ResponseEntity
                .status(httpStatus)
                .body(new ResponseError(httpStatus.value(), ex.getMessage()));
    }

    @ExceptionHandler
    public ResponseEntity<ResponseError> handleViolationException(ValidationException ex) {
        HttpStatus httpStatus = HttpStatus.BAD_REQUEST;
        return ResponseEntity
                .status(httpStatus)
                .body(new ResponseError(httpStatus.value(), ex.getMessage()));
    }

    @ExceptionHandler
    public ResponseEntity<ResponseError> handleSecurityException(SecurityException ex) {
        HttpStatus httpStatus = HttpStatus.FORBIDDEN;
        return ResponseEntity
                .status(httpStatus)
                .body(new ResponseError(httpStatus.value(), ex.getMessage()));
    }

    @ExceptionHandler
    public ResponseEntity<ResponseError> handleMethodArgumentNotValidException(MethodArgumentNotValidException ex) {
        HttpStatus httpStatus = HttpStatus.FORBIDDEN;
        String defaultMessage = ex.getBindingResult().getAllErrors().get(0).getDefaultMessage();
        return ResponseEntity
                .status(httpStatus)
                .body(new ResponseError(httpStatus.value(), defaultMessage));
    }
}