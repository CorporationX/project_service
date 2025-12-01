package faang.school.projectservice.controller.handler;

import faang.school.projectservice.exception.FileProcessingException;
import faang.school.projectservice.exception.FileStorageException;
import faang.school.projectservice.exception.StorageLimitException;
import jakarta.persistence.EntityNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Optional;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler(EntityNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse handleEntityNotFoundException(EntityNotFoundException e, HttpServletRequest req) {
        log.warn("Entity not found exception at {}, {}: {}",
                safeMethod(req), safeUri(req), e.getMessage());
        return ErrorResponseFactory.create(e, req, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(ObjectOptimisticLockingFailureException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public ErrorResponse handleObjectOptimisticLockingFailureException(
            ObjectOptimisticLockingFailureException e,
            HttpServletRequest req) {
        log.error("Cannot write to repository safety at {}, {}: {}",
                safeMethod(req), safeUri(req), HttpStatus.CONFLICT);
        return ErrorResponseFactory.create(e, req, HttpStatus.CONFLICT);
    }

    @ExceptionHandler(FileProcessingException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleFileProcessingException(FileProcessingException e, HttpServletRequest req) {
        log.warn("File processing failed at {}, {}: {}",
                safeMethod(req), safeUri(req), e.getMessage(), e);
        return ErrorResponseFactory.create(e, req, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(StorageLimitException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleStorageLimitException(StorageLimitException e, HttpServletRequest req) {
        log.warn("Storage limit exceeded at {}, {}: {}",
                safeMethod(req), safeUri(req), e.getMessage(), e);
        return ErrorResponseFactory.create(e, req, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(FileStorageException.class)
    @ResponseStatus(HttpStatus.SERVICE_UNAVAILABLE)
    public ErrorResponse handleFileStorageException(FileStorageException e, HttpServletRequest req) {
        log.error("File storage error at {}, {}: {}",
                safeMethod(req), safeUri(req), e.getMessage(), e);
        return ErrorResponseFactory.create(e, req, HttpStatus.SERVICE_UNAVAILABLE);
    }

    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ErrorResponse handleUnexpectedException(Exception e, HttpServletRequest req) {
        log.error("Unexpected error ({}) at {}, {}: {}",
                e.getClass().getSimpleName(), safeMethod(req), safeUri(req), e.getMessage(), e);

        return ErrorResponseFactory.create(e, req, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    private String safeMethod(HttpServletRequest req) {
        return Optional.ofNullable(req).map(HttpServletRequest::getMethod).orElse("N/A");
    }

    private String safeUri(HttpServletRequest req) {
        return Optional.ofNullable(req).map(HttpServletRequest::getRequestURI).orElse("N/A");
    }
}
