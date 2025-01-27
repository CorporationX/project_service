package faang.school.projectservice.exception;

public class DataValidationException extends RuntimeException {
    public DataValidationException(String error) {
        super(error);
    }
}
