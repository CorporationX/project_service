package faang.school.projectservice.advice;

import faang.school.projectservice.exception.ValidException;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.List;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {
    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public List<String> validErrors(MethodArgumentNotValidException e) {
        List<String> error = e.getFieldErrors().stream().map(DefaultMessageSourceResolvable::getDefaultMessage).toList();
        log.error("Data validation exception occurred: {}", error);
        return error;
    }

    @ExceptionHandler(ValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public String validError(ValidException e) {
        return e.getMessage();
    }

    @ExceptionHandler(ConstraintViolationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public List<String> constraintViolationException(ConstraintViolationException e) {
        return e.getConstraintViolations().stream().map(ConstraintViolation::getMessage).toList();
    }
}
