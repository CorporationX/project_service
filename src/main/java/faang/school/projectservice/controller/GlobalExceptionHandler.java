package faang.school.projectservice.controller;

import faang.school.projectservice.dto.error.ErrorResponseDto;
import faang.school.projectservice.dto.error.ErrorType;
import faang.school.projectservice.exception.DataValidationException;
import jakarta.persistence.EntityNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(DataValidationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponseDto handleDataValidationException(DataValidationException e, HttpServletRequest request) {
        log.error("Validation Error", e);
        return new ErrorResponseDto(
                ErrorType.VALIDATION_ERROR.getMessage(),
                e.getMessage(),
                HttpStatus.BAD_REQUEST.value(),
                request.getRequestURI()
        );
    }

    @ExceptionHandler(EntityNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponseDto handleEntityNotFoundException(EntityNotFoundException e, HttpServletRequest request) {
        log.error("Requested Entity Not Found");
        return new ErrorResponseDto(
                ErrorType.NOT_FOUND.getMessage(),
                e.getMessage(),
                HttpStatus.NOT_FOUND.value(),
                request.getRequestURI()
        );
    }

    @ExceptionHandler(IllegalStateException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ErrorResponseDto handleIllegalStateException(IllegalStateException e, HttpServletRequest request) {
        log.error("Project could not be retrieved (null returned)", e);
        return new ErrorResponseDto(
                ErrorType.ILLEGAL_STATE.getMessage(),
                e.getMessage(),
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                request.getRequestURI()
        );
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponseDto handleMethodArgumentNotValidException(MethodArgumentNotValidException e, HttpServletRequest request) {
        log.error("Validation Error", e);
        Map<String, String> errors = new HashMap<>();
        e.getBindingResult().getAllErrors().forEach((error) -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });
        return new ErrorResponseDto(
                ErrorType.VALIDATION_ERROR.getMessage(),
                e.getMessage(),
                HttpStatus.BAD_REQUEST.value(),
                request.getRequestURI(),
                errors
        );
    }
}
