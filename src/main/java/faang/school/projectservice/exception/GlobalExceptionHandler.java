package faang.school.projectservice.exception;

import faang.school.projectservice.dto.error.ErrorResponse;
import jakarta.persistence.EntityNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.util.HashMap;
import java.util.Map;

@Slf4j
/*
@RestControllerAdvice(basePackages = "faang.school.projectservice")
*/
@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(IllegalEntityException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse illegalEntityException(IllegalEntityException exception) {
        return new ErrorResponse(HttpStatus.BAD_REQUEST.value(), exception.getMessage());
    }

    @ExceptionHandler(EntityNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse entityNotFoundException(EntityNotFoundException exception) {
        return new ErrorResponse(HttpStatus.NOT_FOUND.value(), exception.getMessage());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse methodArgumentNotValidException(MethodArgumentNotValidException exception) {
        Map<String, String> errors = new HashMap<>();

        exception.getBindingResult().getAllErrors().forEach(error -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });

        StringBuilder sb = new StringBuilder();
        errors.forEach((key, value) -> sb.append("%s; ".formatted(value)));
        return new ErrorResponse(HttpStatus.BAD_REQUEST.value(), sb.toString());
    }

    @ExceptionHandler(JiraException.class)
    public ErrorResponse handleJiraErrorResponseException(JiraException ex) {
        log.error("{}, {}", ex.getStatus(), ex.getMessage());
        return new ErrorResponse(ex.getStatus().value(), ex.getMessage());
    }
}
