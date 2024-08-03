package faang.school.projectservice.controller;

import faang.school.projectservice.exception.AccessDeniedException;
import faang.school.projectservice.exception.ErrorResponse;
import faang.school.projectservice.exception.StorageSizeException;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.time.LocalDateTime;

@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(StorageSizeException.class)
    @ResponseStatus(HttpStatus.PAYLOAD_TOO_LARGE)
    public ErrorResponse handleStorageSizeException(StorageSizeException e, HttpServletRequest rq){
        return buildErrorResponse(e,rq,HttpStatus.PAYLOAD_TOO_LARGE,"Storage error",e.getMessage());
    }

    @ExceptionHandler(AccessDeniedException.class)
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public ErrorResponse handleAccessDeniedException(AccessDeniedException e, HttpServletRequest rq){
        return buildErrorResponse(e,rq,HttpStatus.FORBIDDEN,"Access Denied",e.getMessage());
    }
    @ExceptionHandler(RuntimeException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ErrorResponse handleRuntimeException(RuntimeException e, HttpServletRequest rq){
        return buildErrorResponse(e,rq,HttpStatus.INTERNAL_SERVER_ERROR,"Internal Server Error",e.getMessage());
    }
        private ErrorResponse buildErrorResponse(Exception e, HttpServletRequest rq, HttpStatus status, String error, String message){
        return ErrorResponse.builder()
                .timestamp(LocalDateTime.now())
                .url(rq.getRequestURI())
                .status(status.value())
                .error(error)
                .message(message)
                .build();
        }
}
