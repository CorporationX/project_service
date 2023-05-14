package faang.school.projectservice.controller;

import faang.school.projectservice.dto.Error;
import faang.school.projectservice.exception.BusinessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<Error> handleBusinessException(BusinessException e) {
        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(fromBusinessException(e));
    }

    private Error fromBusinessException(BusinessException e) {
        return new Error(
                e.getCode(),
                e.getMessage()
        );
    }
}
