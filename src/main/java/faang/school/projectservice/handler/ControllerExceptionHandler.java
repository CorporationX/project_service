package faang.school.projectservice.handler;

import faang.school.projectservice.dto.ErrorResponseDto;
import jakarta.persistence.EntityNotFoundException;
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

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponseDto handleInvalidData(MethodArgumentNotValidException e) {
        log.error("Invalid data from request given", e);
        String message = Arrays.stream(e.getDetailMessageArguments()).map(Object::toString).collect(Collectors.joining("; "));
        return new ErrorResponseDto(
                HttpStatus.BAD_REQUEST.name(),
                "Invalid data provided.",
                message,
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
