package faang.school.projectservice.controller;

import faang.school.projectservice.exception.project.DuplicateResourceException;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(DuplicateResourceException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    static ErrorResponse handleDuplicateResource(DuplicateResourceException ex) {
        return new ErrorResponse(ex.getMessage());
    }

    @Data
    @AllArgsConstructor
    static class ErrorResponse {
        private String error;
    }
}
