package faang.school.projectservice.exception;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class DataValidationException extends RuntimeException{

    public DataValidationException(String message) {
        super(message);
    }
}
