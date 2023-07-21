package faang.school.projectservice.exception;

public class DataValidationException extends RuntimeException {
    public DataValidationException() {
    }

    public DataValidationException(String message) {
        super(message);
    }
}
