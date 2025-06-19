package faang.school.projectservice.exception.common;

public class DataValidationException extends RuntimeException {
    public DataValidationException(String message) {
        super(message);
    }
}