package faang.school.projectservice.exception;

import jakarta.persistence.EntityNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {
    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Map<String, String> handleValidationException(MethodArgumentNotValidException e) {
        log.error("MethodArgumentNotValidException", e);
        return e.getBindingResult().getAllErrors().stream()
                .collect(Collectors.toMap(
                        error -> ((FieldError) error).getField(),
                                error -> Objects.requireNonNullElse(error.getDefaultMessage(), ""))
                        );
    }

    @ExceptionHandler(DataValidationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    ErrorResponse handleDataValidationException(DataValidationException e) {
        log.error("DataValidationException", e);
        return new ErrorResponse(LocalDateTime.now(), e.getMessage());
    }

    @ExceptionHandler(EntityNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    ErrorResponse handleEntityNotFoundException(EntityNotFoundException e) {
        log.error("EntityNotFoundException", e);
        return new ErrorResponse(LocalDateTime.now(), e.getMessage());
    }

    @ExceptionHandler(RuntimeException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    ErrorResponse handleRuntimeException(RuntimeException e) {
        log.error("RuntimeException", e);
        return new ErrorResponse(LocalDateTime.now(), e.getMessage());
    }
}
