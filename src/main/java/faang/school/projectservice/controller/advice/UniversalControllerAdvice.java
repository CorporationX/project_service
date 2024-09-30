package faang.school.projectservice.controller.advice;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

@RestControllerAdvice
@Slf4j
public class UniversalControllerAdvice {

    private final Map<Class<? extends Exception>, HttpStatus> exceptionStatusMap = Map.of(
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

        Map<String, String> errors = null;

        if (ex instanceof MethodArgumentNotValidException) {
            errors = createValidationErrorsMap((MethodArgumentNotValidException) ex);
        }

        return new ResponseEntity<>(createErrorResponse(status, message, errors), status);
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

    private ErrorResponse createErrorResponse(HttpStatus status, String message, Map<String, String> errors) {
        return new ErrorResponse(status.value(), message, errors);
    }
}