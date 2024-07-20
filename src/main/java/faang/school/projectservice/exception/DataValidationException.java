package faang.school.projectservice.exception;

import org.springframework.stereotype.Component;

@Component
public class DataValidationException extends RuntimeException{
    public DataValidationException(String message) {
        super(message);
    }
}
