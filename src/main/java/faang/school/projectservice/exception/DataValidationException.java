package faang.school.projectservice.exception;

import org.springframework.web.bind.annotation.RestControllerAdvice;


public class DataValidationException extends RuntimeException {

    public DataValidationException(String message) {
        super(message);
    }
}

