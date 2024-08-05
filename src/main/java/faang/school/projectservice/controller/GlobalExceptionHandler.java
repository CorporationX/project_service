package faang.school.projectservice.controller;

import faang.school.projectservice.exception.AccessDeniedException;
import faang.school.projectservice.exception.ErrorResponse;
import faang.school.projectservice.exception.StorageSizeException;
import jakarta.persistence.EntityNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.multipart.MaxUploadSizeExceededException;

import java.time.LocalDateTime;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {
    @Value("${spring.servlet.multipart.max-file-size}")
    private String maxFileSize;

    @ExceptionHandler(StorageSizeException.class)
    @ResponseStatus(HttpStatus.PAYLOAD_TOO_LARGE)
    public ErrorResponse handleStorageSizeException(StorageSizeException e, HttpServletRequest rq) {
        return buildErrorResponse(e, rq, HttpStatus.PAYLOAD_TOO_LARGE, "Storage error", e.getMessage());
    }

    @ExceptionHandler(AccessDeniedException.class)
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public ErrorResponse handleAccessDeniedException(AccessDeniedException e, HttpServletRequest rq) {
        return buildErrorResponse(e, rq, HttpStatus.FORBIDDEN, "Access Denied", e.getMessage());
    }

    @ExceptionHandler(EntityNotFoundException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse EntityNotFoundException(EntityNotFoundException e, HttpServletRequest rq) {
        return buildErrorResponse(e, rq, HttpStatus.BAD_REQUEST, "Bad request", e.getMessage());
    }

    @ExceptionHandler(IllegalArgumentException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse IllegalArgumentException(IllegalArgumentException e, HttpServletRequest rq) {
        return buildErrorResponse(e, rq, HttpStatus.BAD_REQUEST, "Bad request", e.getMessage());
    }

    @ExceptionHandler(RuntimeException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ErrorResponse handleRuntimeException(RuntimeException e, HttpServletRequest rq) {
        return buildErrorResponse(e, rq, HttpStatus.INTERNAL_SERVER_ERROR, "Internal Server Error", e.getMessage());
    }

    @ExceptionHandler(MaxUploadSizeExceededException.class)
    @ResponseStatus(HttpStatus.PAYLOAD_TOO_LARGE)
    public ErrorResponse handleMaxUploadSizeException(MaxUploadSizeExceededException e, HttpServletRequest rq) {
        return buildErrorResponse(e, rq, HttpStatus.PAYLOAD_TOO_LARGE, "Bad request", "File too big. Max upload size " + maxFileSize);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleMethodArgumentNotValidException(MethodArgumentNotValidException e, HttpServletRequest rq) {
        return buildErrorResponse(e, rq, HttpStatus.BAD_REQUEST, "Invalid argument", e.getMessage());
    }

    @ExceptionHandler(ConstraintViolationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleConstraintViolationException(ConstraintViolationException e, HttpServletRequest rq) {
        return buildErrorResponse(e, rq, HttpStatus.BAD_REQUEST, "Invalid argument", e.getMessage());
    }


    private ErrorResponse buildErrorResponse(Exception e, HttpServletRequest rq, HttpStatus status, String error, String message) {
        log.error(e.getClass().toString(), e);
        return ErrorResponse.builder()
                .timestamp(LocalDateTime.now())
                .url(rq.getRequestURI())
                .status(status.value())
                .error(error)
                .message(message)
                .build();
    }

}
