package faang.school.projectservice.controller;

import com.amazonaws.services.kms.model.NotFoundException;
import com.atlassian.jira.rest.client.api.RestClientException;
import faang.school.projectservice.exception.errorResponse.ErrorResponse;
import faang.school.projectservice.exception.errorResponse.JiraErrorResponse;
import org.springframework.http.HttpStatus;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Map<String, String> handlerMethodArgumentNotValidException(MethodArgumentNotValidException exception) {
        return exception.getBindingResult()
                .getFieldErrors()
                .stream()
                .collect(Collectors.toMap(FieldError::getField, FieldError::getDefaultMessage));
    }

    @ExceptionHandler(IllegalArgumentException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleIllegalArgumentException(IllegalArgumentException exception) {
        return new ErrorResponse(exception.getMessage());
    }

    @ExceptionHandler(NotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse handlerNotFoundException(NotFoundException exception) {
        return new ErrorResponse(exception.getMessage());
    }

    @ExceptionHandler(RestClientException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public JiraErrorResponse handlerRestClientException(RestClientException exception) {
        List<String> messages = new ArrayList<>();
        Map<String, String> errors = new HashMap<>();

        exception.getErrorCollections().forEach(errorCollection -> {
            messages.addAll(errorCollection.getErrorMessages());
            errors.putAll(errorCollection.getErrors());
        });

        return new JiraErrorResponse(messages, errors);
    }
}