package faang.school.projectservice.exception;

public class DataValidationException extends RuntimeException {

    public DataValidationException(ErrorMessage errorMessage, Object... arguments) {
        super(String.format(errorMessage.getMessage(), arguments));
    }

    public DataValidationException(String message ) {
        super(message);
    }
}
