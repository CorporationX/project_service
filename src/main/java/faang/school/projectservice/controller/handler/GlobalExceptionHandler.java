package faang.school.projectservice.controller.handler;

import faang.school.projectservice.exception.AccessDeniedProjectException;
import faang.school.projectservice.exception.FileException;
import faang.school.projectservice.exception.StorageLimitExceededException;
import jakarta.persistence.EntityNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(FileException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ErrorResponse handleFileException(FileException e) {
        log.error("Exception handled: {}", e.getClass().getSimpleName(), e);
        return buildResponse(e);
    }

    @ExceptionHandler(AccessDeniedProjectException.class)
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public ErrorResponse handleAccessDeniedProjectException(AccessDeniedProjectException e) {
        log.error("Exception handled: {}", e.getClass().getSimpleName(), e);
        return buildResponse(e);
    }

    @ExceptionHandler(StorageLimitExceededException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleStorageLimitExceededException(StorageLimitExceededException e) {
        log.error("Exception handled: {}", e.getClass().getSimpleName(), e);
        return buildResponse(e);
    }

    @ExceptionHandler(EntityNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse handleEntityNotFoundException(EntityNotFoundException e) {
        log.error("Exception handled: {}", e.getClass().getSimpleName(), e);
        return buildResponse(e);
    }

    private ErrorResponse buildResponse(Exception e) {
        log.error(e.getClass().getSimpleName(), e);
        return ErrorResponse.builder()
                .timestamp(LocalDateTime.now())
                .error(e.getClass().getSimpleName())
                .message(e.getMessage())
                .build();
    }
}
