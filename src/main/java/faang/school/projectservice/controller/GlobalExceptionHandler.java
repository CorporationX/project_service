package faang.school.projectservice.controller;

import faang.school.projectservice.exception.*;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;

import java.util.List;
import java.util.Map;
import java.util.Objects;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleMethodArgumentNotValidException(MethodArgumentNotValidException ex, WebRequest request) {
        List<Map<String, String>> fieldErrors = ex.getBindingResult().getFieldErrors().stream()
                .map(fieldError -> Map.of(
                        "field", fieldError.getField(),
                        "message", Objects.requireNonNull(fieldError.getDefaultMessage())
                ))
                .toList();

        return ErrorResponse.builder()
                .path(getPath(request))
                .details(fieldErrors)
                .message("Validation failed")
                .build();
    }

    @ExceptionHandler(ProjectAlreadyExistsException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public ErrorResponse handleProjectAlreadyExistsException(ProjectAlreadyExistsException ex, WebRequest request) {
        return ErrorResponse.builder()
                .path(getPath(request))
                .message(ex.getMessage())
                .build();
    }

    @ExceptionHandler({IllegalArgumentException.class, MeetingOwnershipRequiredException.class,PaymentFailedException.class})
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleIllegalArgumentException(IllegalArgumentException ex, WebRequest request) {
        return ErrorResponse.builder()
                .path(getPath(request))
                .message(ex.getMessage())
                .build();
    }

    @ExceptionHandler(EntityNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse handleEntityNotFoundException(EntityNotFoundException ex, WebRequest request) {
        return ErrorResponse.builder()
                .path(getPath(request))
                .message(ex.getMessage())
                .build();
    }

    @ExceptionHandler({RuntimeException.class, PaymentServiceConnectException.class, UserServiceConnectionException.class})
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ErrorResponse handleRuntimeException(RuntimeException ex, WebRequest request) {
        return ErrorResponse.builder()
                .path(getPath(request))
                .message(ex.getMessage())
                .build();
    }

    private String getPath(WebRequest webRequest) {
        return webRequest.getDescription(false).replace("uri=", "");
    }
}
