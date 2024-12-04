package faang.school.projectservice.controller;

import faang.school.projectservice.exceptions.DataValidationException;
import faang.school.projectservice.dto.ErrorResponse;
import faang.school.projectservice.exceptions.FileSizeExceededException;
import faang.school.projectservice.exceptions.ImageProcessingException;
import faang.school.projectservice.exceptions.ResourceNotFoundException;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.orm.jpa.JpaObjectRetrievalFailureException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    public static final String RESOURCE_NOT_FOUND = "ResourceNotFoundException occurred: ";
    public static final String DATA_VALIDATION_ERROR = "DataValidationException occurred: ";
    public static final String ILLEGAL_ARGUMENT = "IllegalArgumentException occurred: ";
    public static final String UNEXPECTED_ERROR = "An unexpected error occurred: ";
    private static final String ENTITY_NOT_FOUND = "EntityNotFoundException: ";
    private static final String CONSTRAINT_VIOLATION = "ConstraintViolationException: ";
    private static final String METHOD_ARGUMENT_NOT_VALID = "ConstraintViolationException: ";
    private static final String FILE_SIZE_EXCEEDED = "FileSizeExceededException: ";
    private static final String IMAGE_PROCESSING_EXCEPTION = "ImageProcessingException: ";

    @ExceptionHandler(ResourceNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse handleResourceNotFoundException(ResourceNotFoundException ex) {
        log.error(RESOURCE_NOT_FOUND, ex);
        return new ErrorResponse(
                HttpStatus.NOT_FOUND.value(),
                ex.getMessage(),
                LocalDateTime.now());
    }

    @ExceptionHandler(ConstraintViolationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleConstraintViolationException(ConstraintViolationException ex) {
        log.error(CONSTRAINT_VIOLATION, ex);
        return new ErrorResponse(
                HttpStatus.BAD_REQUEST.value(),
                ex.getMessage(),
                LocalDateTime.now()
        );
    }

    @ExceptionHandler(DataValidationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleDataValidationException(DataValidationException ex) {
        log.error(DATA_VALIDATION_ERROR, ex);
        return new ErrorResponse(
                HttpStatus.BAD_REQUEST.value(),
                ex.getMessage(),
                LocalDateTime.now()
        );
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleMethodArgumentNotValidException(MethodArgumentNotValidException ex) {
        log.error(METHOD_ARGUMENT_NOT_VALID, ex);

        String errorMessage = ex.getBindingResult().getFieldErrors().stream()
                .map(error -> String.format("Field '%s': %s", error.getField(), error.getDefaultMessage()))
                .reduce((msg1, msg2) -> msg1 + "; " + msg2)
                .orElse("Validation error");

        return new ErrorResponse(
                HttpStatus.BAD_REQUEST.value(),
                errorMessage,
                LocalDateTime.now()
        );
    }

    @ExceptionHandler(IllegalArgumentException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleIllegalArgumentException(IllegalArgumentException ex) {
        log.error(ILLEGAL_ARGUMENT, ex);
        return new ErrorResponse(
                HttpStatus.BAD_REQUEST.value(),
                ex.getMessage(),
                LocalDateTime.now()
        );
    }

    @ExceptionHandler({EntityNotFoundException.class, JpaObjectRetrievalFailureException.class})
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse handleEntityNotFoundException(EntityNotFoundException ex) {
        log.error(ENTITY_NOT_FOUND, ex);
        return new ErrorResponse(
                HttpStatus.NOT_FOUND.value(),
                ex.getMessage(),
                LocalDateTime.now()
        );
    }

    @ExceptionHandler(FileSizeExceededException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleFileSizeExceededException(FileSizeExceededException ex) {
        log.error(FILE_SIZE_EXCEEDED, ex);
        return new ErrorResponse(
                HttpStatus.BAD_REQUEST.value(),
                ex.getMessage(),
                LocalDateTime.now()
        );
    }

    @ExceptionHandler(ImageProcessingException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ErrorResponse handleFileUploadException(ImageProcessingException ex) {
        log.error(IMAGE_PROCESSING_EXCEPTION, ex);
        return new ErrorResponse(
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                ex.getMessage(),
                LocalDateTime.now()
        );
    }

    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ErrorResponse handleGenericException(Exception ex) {
        log.error(UNEXPECTED_ERROR, ex);
        return new ErrorResponse(
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                ex.getMessage(),
                LocalDateTime.now()
        );
    }
}

