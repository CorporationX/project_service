package faang.school.projectservice.exception.handler;

import faang.school.projectservice.exception.JiraClientException;
import faang.school.projectservice.exception.ProjectNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.ErrorResponse;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.reactive.function.client.WebClientResponseException;

@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(JiraClientException.class)
    public ResponseEntity<ErrorResponse> handleJiraClientException(JiraClientException exception) {
        ErrorResponse errorResponse = ErrorResponse.builder(exception,
                        exception.getStatusCode(), exception.getMessage())
                .title("Jira service error")
                .property("service", "jira")
                .build();

        return new ResponseEntity<>(errorResponse, exception.getStatusCode());
    }

    @ExceptionHandler(WebClientResponseException.class)
    public ResponseEntity<ErrorResponse> handleWebClientResponseException(WebClientResponseException exception) {
        ErrorResponse errorResponse = ErrorResponse.builder(exception,
                        exception.getStatusCode(), exception.getMessage())
                .title("Jira web client error")
                .property("service", "jira")
                .build();

        return new ResponseEntity<>(errorResponse, exception.getStatusCode());
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorResponse> handleIllegalArgumentException(IllegalArgumentException exception) {
        ErrorResponse errorResponse = ErrorResponse.builder(exception,
                        HttpStatus.BAD_REQUEST, exception.getMessage())
                .title("Invalid input")
                .property("service", "validation")
                .build();

        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(ProjectNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleProjectNotFoundException(ProjectNotFoundException exception) {
        ErrorResponse errorResponse = ErrorResponse.builder(exception,
                        HttpStatus.NOT_FOUND, exception.getMessage())
                .title("Project not found")
                .property("service", "validation")
                .build();

        return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
    }
}
