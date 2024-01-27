package faang.school.projectservice.exception;

/**
 * @author Alexander Bulgakov
 */

public class DataValidationException extends RuntimeException {
    public DataValidationException(String message) {
        super(message);
    }
}
