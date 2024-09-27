package faang.school.projectservice.exception.handler;

import faang.school.projectservice.dto.ErrorResponse;
import faang.school.projectservice.exception.EntityFieldNotFoundException;
import faang.school.projectservice.exception.JiraException;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(JiraException.class)
    public ResponseEntity<ErrorResponse> handleJiraException(JiraException exception) {
        ErrorResponse errorResponse = exception.getErrorResponse();

        List<String> errorMessages = errorResponse.getErrorMessages();
        errorMessages.forEach(log::error);

        Optional.ofNullable(errorResponse.getErrors())
                .ifPresent(errors -> errors.forEach((key, value) -> log.error("{}: {}", key, value)));

        log.error("Http code: {}", exception.getHttpCode().toString(), exception);

        return ResponseEntity
                .status(exception.getHttpCode())
                .body(errorResponse);
    }

    @ExceptionHandler(EntityFieldNotFoundException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleEntityFieldNotFoundException(EntityFieldNotFoundException exception) {
        String message = exception.getMessage();
        log.error(message, exception);
        return ErrorResponse.builder()
                .errorMessages(List.of(message))
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
