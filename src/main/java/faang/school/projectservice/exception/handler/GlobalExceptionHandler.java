package faang.school.projectservice.exception.handler;


import faang.school.projectservice.dto.ErrorResponse;
import faang.school.projectservice.exception.DataValidationException;
import jakarta.persistence.EntityNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import faang.school.projectservice.dto.ErrorResponse;
import faang.school.projectservice.exception.JiraException;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import java.util.NoSuchElementException;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @Value("${project.service.name}")
    private String serviceName;

    @ExceptionHandler({DataValidationException.class})
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleDataExceptions(RuntimeException exception) {
        String message = exception.getMessage();
        log.error("Error: {}", message);
        return ErrorResponse.builder()
                .serviceName(serviceName)
                .globalMessage(message)
                .status(HttpStatus.BAD_REQUEST.value())
                .build();
    }

    @ExceptionHandler({EntityNotFoundException.class, NoSuchElementException.class})
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse handleEntityNotFoundException(RuntimeException exception) {
        String message = exception.getMessage();
        log.error("Entity not found: {}", message, exception);
        return ErrorResponse.builder()
                .serviceName(serviceName)
                .globalMessage(message)
                .status(HttpStatus.NOT_FOUND.value())
                .build();
    }

    @ExceptionHandler(IllegalAccessException.class)
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public ErrorResponse handleIllegalAccessException(IllegalAccessException exception) {
        String message = exception.getMessage();
        log.error("Access denied: {}", message);
        return ErrorResponse.builder()
                .serviceName(serviceName)
                .globalMessage(message)
                .status(HttpStatus.FORBIDDEN.value())

    @ExceptionHandler(JiraException.class)
    public ResponseEntity<ErrorResponse> handleJiraException(JiraException exception) {
        ErrorResponse errorResponse = exception.getErrorResponse();

        log.error(errorResponse.getGlobalMessage());
        Optional.ofNullable(errorResponse.getErrors())
                .ifPresent(errors -> log.error(errors.toString()));

        log.error("Http code: {}", exception.getHttpCode().toString(), exception);

        return ResponseEntity
                .status(exception.getHttpCode())
                .body(errorResponse);
    }

    @ExceptionHandler(EntityNotFoundException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleEntityNotFoundException(EntityNotFoundException exception) {
        String message = exception.getMessage();
        log.error(message, exception);
        return ErrorResponse.builder()
                .globalMessage(message)
                .build();
    }

    @ExceptionHandler(ConstraintViolationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleConstraintViolationException(ConstraintViolationException exception) {
        Map<String, String> errors = exception.getConstraintViolations()
                .stream()
                .collect(Collectors.toMap(
                        violation -> {
                            String fieldName = violation.getPropertyPath().toString();
                            return fieldName.substring(fieldName.lastIndexOf('.') + 1);
                        },
                        violation -> Optional.ofNullable(violation.getMessage()).orElse("Unknown error")
                ));
        log.error("Constraint violation error: {}", errors);

        return ErrorResponse.builder()
                .errors(errors)
                .status(HttpStatus.BAD_REQUEST.value())
                .build();
    }
}
