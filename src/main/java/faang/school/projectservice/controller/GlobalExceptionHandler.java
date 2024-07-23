package faang.school.projectservice.controller;

import faang.school.projectservice.exception.DataValidationException;
import jakarta.persistence.EntityNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler(DataValidationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public String handlerDataValidationException(DataValidationException e) {
        log.info(e.getMessage(), e + " Method handlerDataValidationException");
        return e.getMessage();
    }

    @ExceptionHandler(EntityNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public String handlerEntityNotFoundException(EntityNotFoundException e) {
        log.info(e.getMessage(), e + " Method handlerEntityNotFoundException");
        return e.getMessage();
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Map<String, String> handlerMethodArgumentNotValidException(MethodArgumentNotValidException e) {
        return e.getBindingResult().getFieldErrors().stream()
                .collect(Collectors.toMap(FieldError::getField,
                        fieldError -> Objects.requireNonNull(fieldError.getDefaultMessage(), " ")
                ));
    }
}
