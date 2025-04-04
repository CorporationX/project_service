package faang.school.projectservice.exception.handler;

import faang.school.projectservice.exception.JiraClientException;
import faang.school.projectservice.exception.ProjectNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.ErrorResponse;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(JiraClientException.class)
    public ResponseEntity<ErrorResponse> handleJiraClientException(JiraClientException exception) {
        String message = "We were unable to connect to the Jira service. Please try again later.";
        ErrorResponse errorResponse = ErrorResponse.builder(exception,
                        exception.getStatusCode(), exception.getMessage())
                .title("Something went wrong with Jira service.")
                .detail(message)
                .property("service", "jira")
                .build();

        return new ResponseEntity<>(errorResponse, exception.getStatusCode());
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorResponse> handleIllegalArgumentException(IllegalArgumentException exception) {
        String message = "It looks like the information you provided is incorrect or incomplete. " +
                "Please check your input and try again.";
        ErrorResponse errorResponse = ErrorResponse.builder(exception,
                        HttpStatus.BAD_REQUEST, exception.getMessage())
                .title("Invalid input detected.")
                .detail(message)
                .property("service", "validation")
                .build();

        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(ProjectNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleProjectNotFoundException(ProjectNotFoundException exception) {
        String message = "We couldn't find the project you're looking for. " +
                "Please ensure the project ID is correct and try again.";
        ErrorResponse errorResponse = ErrorResponse.builder(exception,
                        HttpStatus.NOT_FOUND, exception.getMessage())
                .title("Project not found.")
                .detail(message)
                .property("service", "validation")
                .build();

        return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
    }
}
