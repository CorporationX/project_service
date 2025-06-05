package faang.school.projectservice.handler;

import faang.school.projectservice.dto.ErrorResponseDto;
import faang.school.projectservice.exception.AccessDeniedException;
import faang.school.projectservice.exception.DataValidationException;
import faang.school.projectservice.exception.FileException;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.stream.Collectors;

@Slf4j
@RestControllerAdvice
public class ControllerExceptionHandler {
    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    @ExceptionHandler(ConstraintViolationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponseDto handleConstraintViolation(ConstraintViolationException e) {
        log.error("Constraint violation", e);
        String errorMessage = e.getConstraintViolations().stream()
                .map(ConstraintViolation::getMessage)
                .collect(Collectors.joining("; "));
        return new ErrorResponseDto(
                HttpStatus.BAD_REQUEST.name(),
                "Invalid data provided.",
                errorMessage,
                LocalDateTime.now().format(formatter)
        );
    }

    @ExceptionHandler(AccessDeniedException.class)
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public ErrorResponseDto handleAccessDenied(AccessDeniedException e) {
        log.error("Access denied", e);
        return new ErrorResponseDto(
                HttpStatus.FORBIDDEN.name(),
                "Have no permission.",
                e.getMessage(),
                LocalDateTime.now().format(formatter)
        );
    }

    @ExceptionHandler(FileException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponseDto handleFileException(FileException e) {
        log.error("Error on file logic processing", e);
        return new ErrorResponseDto(
                HttpStatus.BAD_REQUEST.name(),
                "File exception.",
                e.getMessage(),
                LocalDateTime.now().format(formatter)
        );
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponseDto handleInvalidData(MethodArgumentNotValidException e) {
        log.error("Invalid data from request given", e);
        String errorMessage = Arrays.stream(e.getDetailMessageArguments())
                .map(Object::toString)
                .collect(Collectors.joining("; "));
        return new ErrorResponseDto(
                HttpStatus.BAD_REQUEST.name(),
                "Invalid data provided.",
                errorMessage,
                LocalDateTime.now().format(formatter)
        );
    }

    @ExceptionHandler(EntityNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponseDto handleNotFound(EntityNotFoundException e) {
        log.error("Entity not found", e);
        return new ErrorResponseDto(
                HttpStatus.NOT_FOUND.name(),
                "Resource not found.",
                e.getMessage(),
                LocalDateTime.now().format(formatter)
        );
    }

    @ExceptionHandler(DataValidationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponseDto handleDataValidation(DataValidationException e) {
        log.error("Invalid data provided", e);
        return new ErrorResponseDto(
                HttpStatus.BAD_REQUEST.name(),
                "Invalid data provided.",
                e.getMessage(),
                LocalDateTime.now().format(formatter)
        );
    }

    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ErrorResponseDto handleException(Exception e) {
        log.error("Some error occurred.", e);
        return new ErrorResponseDto(
                HttpStatus.INTERNAL_SERVER_ERROR.name(),
                "Something went wrong.",
                e.getMessage(),
                LocalDateTime.now().format(formatter)
        );
    }
}
