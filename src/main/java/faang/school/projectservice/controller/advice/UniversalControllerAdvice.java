package faang.school.projectservice.controller.advice;

import jakarta.persistence.EntityNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
@Slf4j
public class UniversalControllerAdvice {

    private final Map<Class<? extends Exception>, HttpStatus> exceptionStatusMap = Map.of(
            EntityNotFoundException.class, HttpStatus.NOT_FOUND,
            MethodArgumentNotValidException.class, HttpStatus.BAD_REQUEST,
            IllegalArgumentException.class, HttpStatus.BAD_REQUEST,
            IllegalStateException.class, HttpStatus.BAD_REQUEST,
            Exception.class, HttpStatus.INTERNAL_SERVER_ERROR
    );

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleException(Exception ex) {
        log.error(ex.getMessage(), ex);

        HttpStatus status = exceptionStatusMap.getOrDefault(ex.getClass(), HttpStatus.INTERNAL_SERVER_ERROR);
        String message = determineErrorMessage(ex);


        if (ex instanceof MethodArgumentNotValidException) {
            Map<String, String> errors = createValidationErrorsMap((MethodArgumentNotValidException) ex);
            return new ResponseEntity<>(new ErrorResponse(status, message, errors), status);
        }
        return new ResponseEntity<>(new ErrorResponse(status, message, ex), status);
    }

    private String determineErrorMessage(Exception ex) {
        return ex.getMessage() != null ? ex.getMessage() : "An unexpected error occurred";
    }

    private Map<String, String> createValidationErrorsMap(MethodArgumentNotValidException ex) {
        Map<String, String> validationErrors = new HashMap<>();
        ex.getBindingResult().getFieldErrors().forEach(error ->
                validationErrors.put(error.getField(), error.getDefaultMessage())
        );
        return validationErrors;
    }


}