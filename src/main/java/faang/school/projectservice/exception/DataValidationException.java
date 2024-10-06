package faang.school.projectservice.exception;

public class DataValidationException extends RuntimeException {

    public DataValidationException(ExceptionMessage message) {
        super(message.getMessage());
    }

    public DataValidationException(String message) {
        super(message);
    }
}