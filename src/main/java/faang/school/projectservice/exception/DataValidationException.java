package faang.school.projectservice.exception;

public class DataValidationException extends RuntimeException {
    public DataValidationException(ErrorMessage errorMessage) {
        super(errorMessage.getMessage());
    }
}
